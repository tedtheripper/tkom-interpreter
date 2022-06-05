package semcheck;

import executor.ir.*;
import executor.ir.expressions.AsExpression;
import executor.ir.expressions.FunctionCall;
import executor.ir.instructions.*;
import parser.Program;
import parser.expressions.*;
import parser.statements.*;
import semcheck.exception.SemCheckException;

import java.util.*;

public class SemCheck {

    private final Program syntaxTree;
    private Map<String, Function> definedFunctions = new HashMap<>();
    private Map<String, Variable> declaredVariables = new HashMap<>();

    public SemCheck(Program syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    public GlobalBlock check() throws SemCheckException{
        this.definedFunctions.clear();

        scanVariableDeclarations();
        scanFunctionDefinitions();

        return this.traverseTree();
    }

    private void scanFunctionDefinitions() throws SemCheckException {
        var functionNodes = syntaxTree.getStatements().stream()
                .filter(FunctionDef.class::isInstance)
                .map(FunctionDef.class::cast).toList();

        for (var functionNode : functionNodes) {
            if (definedFunctions.containsKey(functionNode.getName())) {
                throw new SemCheckException(String.format("Function: %s duplicate found", functionNode.getName()));
            }
            var function = new Function();
            function.setName(functionNode.getName());
            function.setReturnType(new Type(functionNode.getFunctionReturnType()));
            function.setScope(new Scope());
            for(var param : functionNode.getParameterList()) {
                Variable variable = new Variable(param.getIdentifier(), new Type(param.getType()), param.isMutable());
                if (!function.getScope().addVariable(variable)) {
                    throw new SemCheckException(String.format("In function: %s found duplicate parameter name: %s", function.getName(), param.getIdentifier()));
                }
            }
            definedFunctions.put(
                    functionNode.getName(),
                    function);
        }
    }

    private void scanVariableDeclarations() throws SemCheckException {
        var variableDeclarations = syntaxTree.getStatements().stream()
                .filter(VariableDeclarationStatement.class::isInstance)
                .map(VariableDeclarationStatement.class::cast).toList();

        for (var varDeclaration : variableDeclarations) {
            if (declaredVariables.containsKey(varDeclaration.getName())) {
                throw new SemCheckException(String.format("Variable named: %s redeclaration found", varDeclaration.getName()));
            }
            Variable variable = new Variable(varDeclaration.getName(), new Type(varDeclaration.getType()), varDeclaration.isMutable());
            declaredVariables.put(variable.getName(), variable);
        }
    }

    private GlobalBlock traverseTree() throws SemCheckException {
        Map<String, Function> functions = new HashMap<>();
        var globalScope = new Scope();
        globalScope.setDeclaredVariables(new HashMap<>(declaredVariables));

        var functionNodes = syntaxTree.getStatements().stream()
                .filter(FunctionDef.class::isInstance)
                .map(FunctionDef.class::cast)
                .toList();
        for (var fNode: functionNodes) {
            functions.put(fNode.getName(), checkFunction(globalScope, fNode));
        }
        var instructionNodes = syntaxTree.getStatements().stream()
                .filter(st -> !(st instanceof FunctionDef))
                .toList();

        var block = checkBlock(globalScope, instructionNodes);
        return new GlobalBlock(globalScope, functions, block.getInstructions());
    }


    private Function checkFunction(Scope scope, FunctionDef functionDef) throws SemCheckException {
        var function = definedFunctions.get(functionDef.getName());
        function.getScope().setUpperScope(scope);
        function.setInstructions(checkBlock(scope, functionDef.getStatementsBlock()));
        return function;
    }

    private Block checkBlock(Scope scope, List<Statement> statements) throws SemCheckException {
        var block = new Block();
        block.setScope(new Scope());
        block.getScope().setUpperScope(scope);

        for (var st: statements) {
            if (st instanceof VariableDeclarationStatement variableDeclarationStatement) {
                block.getInstructions().add(checkVarDeclaration(scope, variableDeclarationStatement));
            } else if (st instanceof ReturnStatement returnStatement) {
                block.getInstructions().add(checkReturnStatement(scope, returnStatement));
            } else if (st instanceof JumpLoopStatement jumpLoopStatement) {
                block.getInstructions().add(checkJumpLoopStatement(scope, jumpLoopStatement));
            } else if (st instanceof IfStatement ifStatement) {
                block.getInstructions().add(checkIfStatement(scope, ifStatement));
            } else if (st instanceof WhileStatement whileStatement) {
                block.getInstructions().add(checkWhileStatement(scope, whileStatement));
            }
        }
        return block;
    }

    private Instruction checkJumpLoopStatement(Scope scope, JumpLoopStatement jumpLoopStatement) throws SemCheckException {
        if (!scope.isCanJumpLoop()) {
            throw new SemCheckException("Tried to use break/continue outside of loop");
        }
        var word = jumpLoopStatement.getValue();
        if (word.equals("break")) return new BreakInstruction();
        if (word.equals("continue")) return new ContinueInstruction();
        throw new SemCheckException("Invalid jump loop value");
    }

    private VarDeclaration checkVarDeclaration(Scope scope, VariableDeclarationStatement statement) throws SemCheckException {
        var variable = new Variable(statement.getName(), new Type(statement.getType()), statement.isMutable());
        executor.ir.Expression expression = checkExpression(scope, statement.getExpression());
        variable.setValue(expression);
        if (!scope.addVariable(variable)) {
            throw new SemCheckException(String.format("Variable: %s redeclaration", variable.getName()));
        }
        return new VarDeclaration(variable, expression);
    }

    private executor.ir.Expression checkExpression(Scope scope, parser.expressions.Expression expression) throws SemCheckException {
        if (expression instanceof FunctionCallExpression functionCall) {
            var arguments = functionCall.getArgumentList();
            List<executor.ir.Expression> callArguments = new ArrayList<>();
            for (var arg : arguments) {
                callArguments.add(checkExpression(scope, arg));
            }
            return new FunctionCall(functionCall.getIdentifier(), callArguments);
        } else if (expression instanceof AssignmentExpression assignmentExpression) {
            if (assignmentExpression.getLeftExpression() instanceof Identifier id) {
                if (!scope.hasVariable(id.getName())) {
                    throw new SemCheckException("Assignment to undefined variable");
                }
                if (!scope.getVariable(id.getName()).isMutable()) {
                    throw new SemCheckException("Assignment to constant variable");
                }
                return new executor.ir.expressions.AssignmentExpression(id.getName(), checkExpression(scope, assignmentExpression.getRightExpression()));
            }
            throw new SemCheckException("Left side of the assignment is not an identifier");
        } else if (expression instanceof NullCheckExpression nullCheckExpression) {
            return new executor.ir.expressions.NullCheckExpression(
                    checkExpression(scope, nullCheckExpression.getLeftExpression()),
                    checkExpression(scope, nullCheckExpression.getRightExpression())
            );
        } else if (expression instanceof OrExpression orExpression) {
            return new executor.ir.expressions.OrExpression(
                    checkExpression(scope, orExpression.getLeftExpression()),
                    checkExpression(scope, orExpression.getRightExpression())
            );
        } else if (expression instanceof AndExpression andExpression) {
            return new executor.ir.expressions.AndExpression(
                    checkExpression(scope, andExpression.getLeftExpression()),
                    checkExpression(scope, andExpression.getRightExpression())
            );
        } else if (expression instanceof CompExpression compExpression) {
            return new executor.ir.expressions.CompExpression(
                    checkExpression(scope, compExpression.getLeftExpression()),
                    checkExpression(scope, compExpression.getRightExpression()),
                    compExpression.getOperator().getValue()
            );
        } else if (expression instanceof IsAsExpression isAsExpression) {
            return new AsExpression(
                    checkExpression(scope, isAsExpression.getLeftExpression()),
                    new Type(isAsExpression.getType())
            );
        } else if (expression instanceof AddExpression addExpression) {
            return new executor.ir.expressions.AddExpression(
                    checkExpression(scope, addExpression.getLeftExpression()),
                    checkExpression(scope, addExpression.getRightExpression())
            );
        } else if (expression instanceof SubExpression subExpression) {
            return new executor.ir.expressions.SubExpression(
                    checkExpression(scope, subExpression.getLeftExpression()),
                    checkExpression(scope, subExpression.getRightExpression())
            );
        } else if (expression instanceof MulExpression mulExpression) {
            return new executor.ir.expressions.MulExpression(
                    checkExpression(scope, mulExpression.getLeftExpression()),
                    checkExpression(scope, mulExpression.getRightExpression())
            );
        } else if (expression instanceof DivExpression divExpression) {
            return new executor.ir.expressions.DivExpression(
                    checkExpression(scope, divExpression.getLeftExpression()),
                    checkExpression(scope, divExpression.getRightExpression())
            );
        } else if (expression instanceof DivIntExpression divIntExpression) {
            return new executor.ir.expressions.DivIntExpression(
                    checkExpression(scope, divIntExpression.getLeftExpression()),
                    checkExpression(scope, divIntExpression.getRightExpression())
            );
        } else if (expression instanceof ModExpression modExpression) {
            return new executor.ir.expressions.ModExpression(
                    checkExpression(scope, modExpression.getLeftExpression()),
                    checkExpression(scope, modExpression.getRightExpression())
            );
        } else if (expression instanceof UnaryExpression unaryExpression) {
            return new executor.ir.expressions.UnaryExpression(
                    unaryExpression.getOperator().getValue(),
                    checkExpression(scope, unaryExpression.getExpression())
            );
        } else if (expression instanceof BaseExpression baseExpression) {
            return new executor.ir.expressions.BaseExpression(
                    checkExpression(scope, baseExpression.getExpression())
            );
        } else if (expression instanceof Identifier identifier) {
            return new executor.ir.expressions.Identifier(identifier.getName());
        } else {
            throw new SemCheckException("Invalid expression");
        }
    }

    private void checkAssignment() {}

    private void checkVariable() {}

    private ReturnInstruction checkReturnStatement(Scope scope, ReturnStatement statement) throws SemCheckException {
        if (!scope.isCanDoReturn()) {
            throw new SemCheckException("Tried to use return outside of function");
        }
        return new ReturnInstruction(checkExpression(scope, statement.getExpression()));
    }

    private IfInstruction checkIfStatement(Scope scope, IfStatement statement) throws SemCheckException {
        return new IfInstruction(
                checkExpression(scope, statement.getIfBlock().getExpression()),
                checkBlock(scope, statement.getIfBlock().getStatements()),
                statement.getElseBlock() == null ? null : checkBlock(scope, statement.getElseBlock().getStatements())
        );
    }

    private WhileInstruction checkWhileStatement(Scope scope, WhileStatement statement) throws SemCheckException {
        scope.setCanJumpLoop(true);
        return new WhileInstruction(
                checkExpression(scope, statement.getExpression()),
                checkBlock(scope, statement.getStatements())
        );
    }

}
