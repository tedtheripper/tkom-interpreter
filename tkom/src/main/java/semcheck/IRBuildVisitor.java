package semcheck;

import executor.ir.Expression;
import executor.ir.*;
import executor.ir.expressions.*;
import executor.ir.instructions.*;
import executor.stdlib.StdLibImpl;
import parser.Parameter;
import parser.Program;
import parser.Type;
import parser.expressions.*;
import parser.expressions.AddExpression;
import parser.expressions.AndExpression;
import parser.expressions.AssignmentExpression;
import parser.expressions.BaseExpression;
import parser.expressions.CompExpression;
import parser.expressions.DivExpression;
import parser.expressions.DivIntExpression;
import parser.expressions.Identifier;
import parser.expressions.InsideMatchCompExpression;
import parser.expressions.InsideMatchTypeExpression;
import parser.expressions.ModExpression;
import parser.expressions.MulExpression;
import parser.expressions.NullCheckExpression;
import parser.expressions.OrExpression;
import parser.expressions.SubExpression;
import parser.expressions.UnaryExpression;
import parser.statements.*;
import semcheck.exception.SemCheckException;

import java.util.*;

public class IRBuildVisitor implements BuildVisitor {

    private final TypeEvaluationVisitor typeEvaluationVisitor;
    private final StdLibImpl stdLib;

    private final Stack<IfInstruction> scopedIfInstructions = new Stack<>();
    private final Stack<WhileInstruction> scopedWhileInstructions = new Stack<>();
    private final Stack<Block> scopedBlocks = new Stack<>();
    private final Stack<Expression> expressions = new Stack<>();

    private GlobalBlock globalBlock;
    private UserFunction currentUserFunctionDef;
    private Variable currentVariable;
    private MatchInstruction currentMatchInstruction;

    private boolean expressionAsInstruction = true;

    private boolean functionReturnType = false;
    private boolean variableType = false;
    private boolean isAsType = false;
    private boolean insideMatchType = false;

    private boolean insideMatchStatementDef = false;


    public IRBuildVisitor() {
        this.typeEvaluationVisitor = new TypeEvaluationVisitor();
        this.stdLib = new StdLibImpl();
    }

    public GlobalBlock export(Program program) throws SemCheckException {
        program.accept(this);
        return globalBlock;
    }

    @Override
    public void visitProgram(Program program) throws SemCheckException {
        globalBlock = new GlobalBlock();
        globalBlock.setGlobalScope(new Scope());
        globalBlock.setFunctions(new HashMap<>());
        globalBlock.getFunctions().putAll(stdLib.getEmbeddedFunctionsDefinitions());
        globalBlock.getGlobalScope().setDefinedFunctions(stdLib.getEmbeddedFunctionsDefinitions());
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(globalBlock.getGlobalScope());
        scopedBlocks.push(newBlock);
        for (var st : program.getStatements()) {
            st.accept(this);
        }
        var block = scopedBlocks.pop();
        globalBlock.setInstructions(block.getInstructions());
        globalBlock.getGlobalScope().setDeclaredVariables(block.getScope().getDeclaredVariables());
        if (!scopedBlocks.isEmpty()) {
            throw new SemCheckException("Invalid stack state");
        }
    }

    @Override
    public void visitFunctionDef(FunctionDef functionDef) throws SemCheckException {
        currentUserFunctionDef = new UserFunction();
        currentUserFunctionDef.setName(functionDef.getName());
        if (globalBlock.getFunctions().containsKey(currentUserFunctionDef.getName())) {
            throw new SemCheckException(String.format("Illegal redefinition of function named: %s found", currentUserFunctionDef.getName()));
        }
        currentUserFunctionDef.setScope(new Scope(globalBlock.getGlobalScope()));
        functionDef.getParameterList().forEach(param -> param.accept(this));
        functionReturnType = true;
        functionDef.getFunctionReturnType().accept(this);
        if (!globalBlock.getGlobalScope().addFunction(currentUserFunctionDef)) {
            throw new SemCheckException(String.format("Illegal redefinition of function named: %s found", currentUserFunctionDef.getName()));
        }
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(currentUserFunctionDef.getScope());
        scopedBlocks.push(newBlock);
        for (var st : functionDef.getStatementsBlock()) {
            st.accept(this);
        }
        var block = scopedBlocks.pop();
        validateReturns(block);
        currentUserFunctionDef.setInstructions(block);
        globalBlock.getFunctions().put(currentUserFunctionDef.getName(), currentUserFunctionDef);
        var func = (UserFunction)globalBlock.getGlobalScope().getFunction(currentUserFunctionDef.getName());
        func.setInstructions(block);
        currentUserFunctionDef = null;
    }

    private boolean validateReturns(Block block) throws SemCheckException {
        return validateReturnsList(block.getInstructions());
    }

    private boolean validateReturnsList(List<Instruction> instructions) throws SemCheckException {
        for(var instruction : instructions) {
            if (instruction instanceof ReturnInstruction) return true;
            if (instruction instanceof IfInstruction ifInstruction) {
                var trueBlock = validateReturns(ifInstruction.getTrueBlock());
                if (ifInstruction.getFalseBlock() != null) {
                    return trueBlock && validateReturns(ifInstruction.getFalseBlock());
                } else {
                    return trueBlock;
                }
            }
            if (instruction instanceof MatchInstruction matchInstruction) {
                var countStatements = 0;
                for (var matchIn : matchInstruction.getMatchStatements()) {
                    if (matchIn.getInstruction() instanceof ReturnInstruction) countStatements++;
                }
                if (countStatements == matchInstruction.getMatchStatements().size()) {
                    return true;
                }
            }
        }
        throw new SemCheckException(String.format("Missing return statement in function: `%s`", currentUserFunctionDef.getName()));
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) throws SemCheckException {
        scopedIfInstructions.push(new IfInstruction());
        ifStatement.getIfBlock().accept(this);
        var elseBlock = ifStatement.getElseBlock();
        if (elseBlock != null) {
            elseBlock.accept(this);
        }
        var ifInstruction = scopedIfInstructions.pop();
        scopedBlocks.peek().getInstructions().add(ifInstruction);
    }

    @Override
    public void visitIfBlock(IfBlock ifBlock) throws SemCheckException {
        expressionAsInstruction = false;
        var beforeExpStackSize = expressions.size();

        ifBlock.getExpression().accept(this);
        var expression = expressions.pop();
        if (!isCorrectType(expression, "bool", scopedBlocks.peek().getScope())) {
            throw new SemCheckException("Invalid type for if condition");
        }
        expressionAsInstruction = true;

        assert expressions.size() == beforeExpStackSize;

        scopedIfInstructions.peek().setCondition(expression);
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(scopedBlocks.peek().getScope());
        scopedBlocks.push(newBlock);

        var beforeStmtStackSize = expressions.size();
        for (var st : ifBlock.getStatements()) {
            st.accept(this);
        }
        var block = scopedBlocks.pop();
        if (expressions.size() != beforeStmtStackSize) {
            throw new SemCheckException("Found redundant expression");
        }
        scopedIfInstructions.peek().setTrueBlock(block);
    }

    @Override
    public void visitElseBlock(ElseBlock elseBlock) throws SemCheckException {
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(scopedBlocks.peek().getScope());
        scopedBlocks.push(newBlock);
        var beforeStmtStackSize = expressions.size();
        for (var st : elseBlock.getStatements()) {
            st.accept(this);
        }
        var block = scopedBlocks.pop();
        if (expressions.size() != beforeStmtStackSize) {
            throw new SemCheckException("Found redundant expression");
        }
        scopedIfInstructions.peek().setFalseBlock(block);
    }

    @Override
    public void visitInsideMatchStatement(InsideMatchStatement insideMatchStatement) throws SemCheckException {
        InsideMatchInstruction currentInsideMatchInstruction = new InsideMatchInstruction();
        var newInsideBlock = new Block();
        newInsideBlock.getScope().setUpperScope(currentMatchInstruction.getScope());
        scopedBlocks.push(newInsideBlock);
        currentInsideMatchInstruction.setDefault(insideMatchStatement.isDefault());
        if (!insideMatchStatement.isDefault()) {
            expressionAsInstruction = false;
            insideMatchStatementDef = true;
            insideMatchStatement.getExpression().accept(this);
            expressionAsInstruction = true;
            insideMatchStatementDef = false;
            var exp = expressions.pop();
            if (!isCorrectType(exp, "bool", scopedBlocks.peek().getScope())) {
                throw new SemCheckException("Invalid type for match condition");
            }
            currentInsideMatchInstruction.setExpression(exp);
        }
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(newInsideBlock.getScope());
        scopedBlocks.push(newBlock);
        insideMatchStatement.getSimpleStatement().accept(this);
        newBlock = scopedBlocks.pop();
        if (newBlock.getInstructions().size() != 1) {
            throw new SemCheckException("Found more/less than one statement inside match option");
        }
        currentInsideMatchInstruction.setInstruction(newBlock.getInstructions().get(0));
        var block = scopedBlocks.pop();
        currentInsideMatchInstruction.setScope(block.getScope());
        currentMatchInstruction.getMatchStatements().add(currentInsideMatchInstruction);
    }

    @Override
    public void visitJumpLoopStatement(JumpLoopStatement jumpLoopStatement) throws SemCheckException {
        if (scopedWhileInstructions.isEmpty()) {
            throw new SemCheckException(String.format("Found %s outside of loop", jumpLoopStatement.getValue()));
        }
        if (jumpLoopStatement.getValue().equals("break")) {
            scopedBlocks.peek().getInstructions().add(new BreakInstruction());
        } else {
            scopedBlocks.peek().getInstructions().add(new ContinueInstruction());
        }
    }

    @Override
    public void visitMatchStatement(MatchStatement matchStatement) throws SemCheckException {
        currentMatchInstruction = new MatchInstruction();
        currentMatchInstruction.setScope(new Scope(scopedBlocks.peek().getScope()));
        currentMatchInstruction.setMatchStatements(new ArrayList<>());
        expressionAsInstruction = false;
        matchStatement.getExpression().accept(this);
        expressionAsInstruction = true;
        var exp = expressions.pop();

        currentMatchInstruction.setExpression(exp);
        currentMatchInstruction.getScope().addVariable(
                new Variable("_", exp.evaluateType(this.typeEvaluationVisitor, scopedBlocks.peek().getScope()), false, exp)
        );
        for(var st : matchStatement.getMatchStatements()) {
            st.accept(this);
        }
        scopedBlocks.peek().getInstructions().add(currentMatchInstruction);
        currentMatchInstruction = null;
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) throws SemCheckException {
        if (currentUserFunctionDef == null) {
            throw new SemCheckException("Found return outside of function");
        }
        var returnInstruction = new ReturnInstruction();
        if (returnStatement.getExpression() != null) {
            expressionAsInstruction = false;
            returnStatement.getExpression().accept(this);
            var exp = expressions.pop();
            expressionAsInstruction = true;
            var expType = exp.evaluateType(typeEvaluationVisitor, scopedBlocks.peek().getScope());
            if (!expType.equals(currentUserFunctionDef.getReturnType())) {
                throw new SemCheckException(String.format("Return types in \"%s\" function do not match", currentUserFunctionDef.getName()));
            }
            returnInstruction.setValue(exp);
        }
        scopedBlocks.peek().getInstructions().add(returnInstruction);
    }

    @Override
    public void visitVariableDeclarationStatement(VariableDeclarationStatement variableDeclarationStatement) throws SemCheckException {
        currentVariable = new Variable();
        currentVariable.setName(variableDeclarationStatement.getName());
        currentVariable.setMutable(variableDeclarationStatement.isMutable());
        variableType = true;
        variableDeclarationStatement.getType().accept(this);
        if (currentVariable.getType().getTypeName().equals("void")) {
            throw new SemCheckException("Cannot declare variable with void type");
        }
        expressionAsInstruction = false;
        variableDeclarationStatement.getExpression().accept(this);
        expressionAsInstruction = true;
        var exp = expressions.pop();

        if (!isCorrectNullableType(exp, currentVariable.getType(), scopedBlocks.peek().getScope())) {
            throw new SemCheckException(String.format("Wrong right side type in declaration of %s variable", currentVariable.getName()));
        }
        currentVariable.setValue(exp);
        var varDeclaration = new VarDeclaration();
        varDeclaration.setVariable(currentVariable);
        varDeclaration.setValue(exp);
        if(!scopedBlocks.peek().getScope().addVariable(currentVariable)) {
            throw new SemCheckException(String.format("Variable with name: %s is already defined in this scope", varDeclaration.getVariable().getName()));
        }
        scopedBlocks.peek().getInstructions().add(varDeclaration);
        currentVariable = null;
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) throws SemCheckException {
        scopedWhileInstructions.push(new WhileInstruction());
        expressionAsInstruction = false;
        whileStatement.getExpression().accept(this);
        var exp = expressions.pop();

        if (!isCorrectType(exp, "bool", scopedBlocks.peek().getScope())) {
            throw new SemCheckException("Invalid type for while condition");
        }
        expressionAsInstruction = true;
        scopedWhileInstructions.peek().setCondition(exp);
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(scopedBlocks.peek().getScope());
        scopedBlocks.push(newBlock);
        for (var st : whileStatement.getStatements()) {
            st.accept(this);
        }
        var block = scopedBlocks.pop();
        scopedWhileInstructions.peek().setStatements(block);
        var whileInstruction = scopedWhileInstructions.pop();
        scopedBlocks.peek().getInstructions().add(whileInstruction);
    }

    @Override
    public void visitAddExpression(AddExpression addExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.AddExpression();
        expressions.push(exp);
        addExpression.getLeftExpression().accept(this);
        addExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.AddExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        exp.evaluateType(this.typeEvaluationVisitor, scopedBlocks.peek().getScope());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitAndExpression(AndExpression andExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.AndExpression();
        expressions.push(exp);
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.AndExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        exp.evaluateType(this.typeEvaluationVisitor, scopedBlocks.peek().getScope());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpression assignmentExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.AssignmentExpression();
        expressions.push(exp);
        var prevExpressionAsInstructionState = expressionAsInstruction;
        expressionAsInstruction = false;
        assignmentExpression.getLeftExpression().accept(this);
        assignmentExpression.getRightExpression().accept(this);
        expressionAsInstruction = prevExpressionAsInstructionState;
        var right = expressions.pop();
        var left = expressions.pop();
        if (!(left instanceof executor.ir.expressions.Identifier id)) {
            throw new SemCheckException("Tried to assign value to non-identifier");
        }
        exp = (executor.ir.expressions.AssignmentExpression) expressions.pop();
        exp.setVariableName(id.getName());
        exp.setRightSide(right);

        if (!scopedBlocks.peek().getScope().hasVariable(exp.getVariableName())) {
            throw new SemCheckException(String.format("Variable %s has not been declared", exp.getVariableName()));
        } else {
            var variable = scopedBlocks.peek().getScope().getVariable(exp.getVariableName());
            if (!variable.isMutable()) {
                throw new SemCheckException("Tried changing value of constant variable");
            }
            scopedBlocks.peek().getScope().addVariable(variable);
        }
        exp.evaluateType(this.typeEvaluationVisitor, scopedBlocks.peek().getScope());

        if (expressionAsInstruction) {
            var instructionExpression = new InstructionExpression();
            instructionExpression.setScope(new Scope(scopedBlocks.peek().getScope()));
            instructionExpression.setExpression(exp);
            scopedBlocks.peek().getInstructions().add(instructionExpression);
        } else {
            expressions.push(exp);
        }
    }

    @Override
    public void visitBaseExpression(BaseExpression baseExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.BaseExpression();
        expressions.push(exp);
        baseExpression.getExpression().accept(this);
        var deeperExpression = expressions.pop();
        exp = (executor.ir.expressions.BaseExpression)expressions.pop();
        exp.setExpression(deeperExpression);
        exp.evaluateType(this.typeEvaluationVisitor, scopedBlocks.peek().getScope());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitBooleanLiteralExpression(BooleanLiteralExpression booleanLiteralExpression) throws SemCheckException {
        var exp = new ConstExpression(new executor.ir.Type(false, "bool"), booleanLiteralExpression.getValue());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitCompExpression(CompExpression compExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.CompExpression();
        exp.setOperator(compExpression.getOperator().getValue());
        expressions.push(exp);
        compExpression.getLeftExpression().accept(this);
        compExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.CompExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);

    }

    @Override
    public void visitDivExpression(DivExpression divExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.DivExpression();
        expressions.push(exp);
        divExpression.getLeftExpression().accept(this);
        divExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.DivExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitDivIntExpression(DivIntExpression divIntExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.DivIntExpression();
        expressions.push(exp);
        divIntExpression.getLeftExpression().accept(this);
        divIntExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.DivIntExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitDoubleLiteralExpression(DoubleLiteralExpression doubleLiteralExpression) throws SemCheckException {
        var exp = new ConstExpression(new executor.ir.Type(false, "double"), doubleLiteralExpression.getValue());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression functionCallExpression) throws SemCheckException {
        var name = functionCallExpression.getIdentifier();
        if (stdLib.hasFunction(name)) {
            buildLibFunctionCall(functionCallExpression);
            return;
        }

        var exp = new FunctionCall();
        exp.setName(functionCallExpression.getIdentifier());
        expressions.push(exp);
        var prevExpressionAsInstructionState = expressionAsInstruction;
        expressionAsInstruction = false;
        for(var e : functionCallExpression.getArgumentList()) {
            e.accept(this);
        }
        expressionAsInstruction = prevExpressionAsInstructionState;
        List<Expression> arguments = new ArrayList<>();
        while(expressions.peek() != exp) {
            arguments.add(expressions.pop());
        }
        Collections.reverse(arguments);
        exp = (FunctionCall) expressions.pop();
        exp.setArguments(arguments);


        if (expressionAsInstruction) {
            var instructionExpression = new InstructionExpression();
            instructionExpression.setExpression(exp);
            scopedBlocks.peek().getInstructions().add(instructionExpression);
        } else {
            expressions.push(exp);
        }
    }

    private void buildLibFunctionCall(FunctionCallExpression functionCallExpression) throws SemCheckException {
        var exp = new LibFunctionCall();
        exp.setName(functionCallExpression.getIdentifier());
        expressions.push(exp);
        var prevExpressionAsInstructionState = expressionAsInstruction;
        expressionAsInstruction = false;
        for(var e : functionCallExpression.getArgumentList()) {
            e.accept(this);
        }
        expressionAsInstruction = prevExpressionAsInstructionState;
        List<Expression> arguments = new ArrayList<>();
        while(expressions.peek() != exp) {
            arguments.add(expressions.pop());
        }
        Collections.reverse(arguments);
        exp = (LibFunctionCall) expressions.pop();
        exp.setArguments(arguments);

        exp.evaluateType(this.typeEvaluationVisitor, scopedBlocks.peek().getScope());

        if (expressionAsInstruction) {
            var instructionExpression = new InstructionExpression();
            instructionExpression.setExpression(exp);
            scopedBlocks.peek().getInstructions().add(instructionExpression);
        } else {
            expressions.push(exp);
        }
    }

    @Override
    public void visitIdentifier(Identifier identifier) throws SemCheckException {
        if (insideMatchStatementDef && globalBlock.getFunctions().containsKey(identifier.getName())) {
            var exp = new FunctionCall(identifier.getName(), List.of(new executor.ir.expressions.Identifier("_")));
            expressions.push(exp);
            return;
        }
        if (!scopedBlocks.peek().getScope().hasVariable(identifier.getName())) {
            throw new SemCheckException(String.format("Variable: %s is not defined", identifier.getName()));
        }
        var exp = new executor.ir.expressions.Identifier(identifier.getName());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitInsideMatchCompExpression(InsideMatchCompExpression insideMatchCompExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.InsideMatchCompExpression();
        exp.setOperator(insideMatchCompExpression.getOperator().getValue());
        expressions.push(exp);
        insideMatchCompExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        exp = (executor.ir.expressions.InsideMatchCompExpression) expressions.pop();
        exp.setExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitInsideMatchTypeExpression(InsideMatchTypeExpression insideMatchTypeExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.InsideMatchTypeExpression();
        throwOnInvalidExpressionUse();
        expressions.push(exp);
        insideMatchType = true;
        if (insideMatchTypeExpression.getType() != null) {
            insideMatchTypeExpression.getType().accept(this);
        } else {
            exp = (executor.ir.expressions.InsideMatchTypeExpression) expressions.pop();
            exp.setType(null);
            expressions.push(exp);
        }
    }

    @Override
    public void visitIntegerLiteralExpression(IntegerLiteralExpression integerLiteralExpression) throws SemCheckException {
        var exp = new ConstExpression(new executor.ir.Type(false, "int"), integerLiteralExpression.getValue());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitIsAsExpression(IsAsExpression isAsExpression) throws SemCheckException {
        if (isAsExpression.getOperator().getValue().equals("is")) {
            var exp = new IsExpression();
            expressions.push(exp);
            isAsExpression.getLeftExpression().accept(this);
            var deeperExp = expressions.pop();
            isAsType = true;
            if (isAsExpression.getType() != null) {
                isAsExpression.getType().accept(this);
            } else {
                isAsType = false;
            }
            exp = (IsExpression) expressions.pop();
            exp.setExpression(deeperExp);
            throwOnInvalidExpressionUse();
            expressions.push(exp);
        } else {
            var exp = new AsExpression();
            expressions.push(exp);
            isAsExpression.getLeftExpression().accept(this);
            var deeperExp = expressions.pop();
            isAsType = true;
            isAsExpression.getType().accept(this);
            exp = (AsExpression) expressions.pop();
            exp.setExpression(deeperExp);
            throwOnInvalidExpressionUse();
            expressions.push(exp);
        }
    }

    @Override
    public void visitModExpression(ModExpression modExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.ModExpression();
        expressions.push(exp);
        modExpression.getLeftExpression().accept(this);
        modExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.ModExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitMulExpression(MulExpression mulExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.MulExpression();
        expressions.push(exp);
        mulExpression.getLeftExpression().accept(this);
        mulExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.MulExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitNullCheckExpression(NullCheckExpression nullCheckExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.NullCheckExpression();
        expressions.push(exp);
        nullCheckExpression.getLeftExpression().accept(this);
        nullCheckExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.NullCheckExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitNullLiteralExpression(NullLiteralExpression nullLiteralExpression) throws SemCheckException {
        var exp = new ConstExpression(null, null);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitOrExpression(OrExpression orExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.OrExpression();
        expressions.push(exp);
        orExpression.getLeftExpression().accept(this);
        orExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.OrExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitStringLiteralExpression(StringLiteralExpression stringLiteralExpression) throws SemCheckException {
        var exp = new ConstExpression(new executor.ir.Type(false, "string"), stringLiteralExpression.getValue());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitSubExpression(SubExpression subExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.SubExpression();
        expressions.push(exp);
        subExpression.getLeftExpression().accept(this);
        subExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.SubExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitUnaryExpression(UnaryExpression unaryExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.UnaryExpression();
        exp.setUnaryOperator(unaryExpression.getOperator().getValue());
        expressions.push(exp);
        unaryExpression.getExpression().accept(this);
        var deeperExp = expressions.pop();
        exp = (executor.ir.expressions.UnaryExpression) expressions.pop();
        exp.setExpression(deeperExp);
        exp.evaluateType(this.typeEvaluationVisitor, scopedBlocks.peek().getScope());
        throwOnInvalidExpressionUse();
        expressions.push(exp);
    }

    @Override
    public void visitType(Type type) {
        if (functionReturnType) {
            currentUserFunctionDef.setReturnType(new executor.ir.Type(type));
            functionReturnType = false;
        } else if (variableType) {
            currentVariable.setType(new executor.ir.Type(type));
            variableType = false;
        } else if (isAsType) {
            var exp = expressions.pop();
            if (exp instanceof IsExpression isExpression) {
                isExpression.setType(type == null ? null : new executor.ir.Type(type));
                expressions.push(isExpression);
            } else if (exp instanceof AsExpression asExpression) {
                asExpression.setType(new executor.ir.Type(type));
                expressions.push(asExpression);
            }
            isAsType = false;
        } else if (insideMatchType) {
            var exp = (executor.ir.expressions.InsideMatchTypeExpression)expressions.pop();
            exp.setType(type == null ? null : new executor.ir.Type(type));
            expressions.push(exp);
            insideMatchType = false;
        }
    }

    @Override
    public void visitParameter(Parameter parameter) {
        currentVariable = new Variable();
        currentVariable.setMutable(parameter.isMutable());
        currentVariable.setName(parameter.getIdentifier());
        variableType = true;
        parameter.getType().accept(this);
        currentUserFunctionDef.getScope().addVariable(currentVariable);
        currentVariable = null;
    }

    private boolean isCorrectType(Expression exp, String expectedTypeName, Scope scope) throws SemCheckException {
        var expType = exp.evaluateType(this.typeEvaluationVisitor, scope);
        if (expType == null) return false;
        return expType.getTypeName().equals(expectedTypeName);
    }

    private boolean isCorrectNullableType(Expression exp, executor.ir.Type type, Scope scope) throws SemCheckException {
        var expType = exp.evaluateType(this.typeEvaluationVisitor, scope);
        if (type.getTypeName() == null) throw new SemCheckException("Invalid type");
        if (expType == null) return type.isNullable();
        return expType.getTypeName().equals(type.getTypeName());
    }

    private void throwOnInvalidExpressionUse() throws SemCheckException {
        if (expressionAsInstruction) {
            throw new SemCheckException("Illegal expression as statement");
        }
    }

}
