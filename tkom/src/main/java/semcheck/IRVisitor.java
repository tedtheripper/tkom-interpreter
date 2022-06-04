package semcheck;

import executor.ir.Expression;
import executor.ir.*;
import executor.ir.expressions.AsExpression;
import executor.ir.expressions.IsExpression;
import executor.ir.instructions.*;
import parser.Parameter;
import parser.Program;
import parser.Type;
import parser.expressions.*;
import parser.statements.*;

import java.util.*;

public class IRVisitor implements Visitor {

    private GlobalBlock globalBlock;
    private Function currentFunctionDef;
    private Map<String, Variable> currentParameterMap;
    private Stack<IfInstruction> scopedIfInstructions = new Stack<>();
    private Stack<WhileInstruction> scopedWhileInstructions = new Stack<>();
    private Stack<Block> scopedBlocks = new Stack<>();
    private Variable currentVariable;
    private Stack<Expression> expressions = new Stack<>();

    private boolean expressionAsInstruction = true;

    private boolean functionReturnType = false;
    private boolean variableType = false;
    private boolean isAsType = false;

    public IRVisitor() {
        // TODO document why this constructor is empty
    }

    public GlobalBlock export(Program program) {
        program.accept(this);

        return globalBlock;
    }

    @Override
    public void visitProgram(Program program) {
        globalBlock = new GlobalBlock();
        globalBlock.setGlobalScope(new Scope());
        globalBlock.setFunctions(new HashMap<>());
        scopedBlocks.push(new Block());
        program.getStatements().forEach(st -> st.accept(this));
        var block = scopedBlocks.pop();
        globalBlock.setInstructions(block.getInstructions());
        if (!scopedBlocks.isEmpty()) {
            // TODO: throw new SemCheckException("STACK ERROR");
        }
    }

    @Override
    public void visitFunctionDef(FunctionDef functionDef) {
        currentFunctionDef = new Function();
        currentFunctionDef.setName(functionDef.getName());
        currentFunctionDef.setScope(new Scope());
        currentParameterMap = new HashMap<>();
        functionDef.getParameterList().forEach(param -> param.accept(this));
        currentFunctionDef.getScope().setDeclaredVariables(currentParameterMap);
        currentParameterMap = null;
        functionReturnType = true;
        functionDef.getFunctionReturnType().accept(this);
        scopedBlocks.push(new Block());
        functionDef.getStatementsBlock().forEach(st -> st.accept(this));
        var block = scopedBlocks.pop();
        currentFunctionDef.setInstructions(block);
        globalBlock.getFunctions().put(currentFunctionDef.getName(), currentFunctionDef);
        currentFunctionDef = null;
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
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
    public void visitIfBlock(IfBlock ifBlock) {
        expressionAsInstruction = false;
        var beforeExpStackSize = expressions.size();

        ifBlock.getExpression().accept(this);
        var expression = expressions.pop();
        expressionAsInstruction = true;

        assert expressions.size() == beforeExpStackSize;

        // TODO: check if anything was added to the stack
        scopedIfInstructions.peek().setCondition(expression);
        scopedBlocks.push(new Block());

        var beforeStmtStackSize = expressions.size();
        ifBlock.getStatements().forEach(st -> st.accept(this));
        var block = scopedBlocks.pop();
        if (expressions.size() != beforeStmtStackSize) {
            // TODO: throw semcheck exception - nadmiarowe expression
        }
        scopedIfInstructions.peek().setTrueBlock(block);
    }

    @Override
    public void visitElseBlock(ElseBlock elseBlock) {
        scopedBlocks.push(new Block());
        elseBlock.getStatements().forEach(st -> st.accept(this));
        var block = scopedBlocks.pop();
        scopedIfInstructions.peek().setFalseBlock(block);
    }

    @Override
    public void visitInsideMatchStatement(InsideMatchStatement insideMatchStatement) {
        // TODO:
        insideMatchStatement.getExpression().accept(this);
        insideMatchStatement.getSimpleStatement().accept(this);
    }

    @Override
    public void visitJumpLoopStatement(JumpLoopStatement jumpLoopStatement) {
        if (jumpLoopStatement.getValue().equals("break")) {
            scopedBlocks.peek().getInstructions().add(new BreakInstruction());
        } else {
            scopedBlocks.peek().getInstructions().add(new ContinueInstruction());
        }
    }

    @Override
    public void visitMatchStatement(MatchStatement matchStatement) {
        expressionAsInstruction = false;
        matchStatement.getExpression().accept(this);
        expressionAsInstruction = true;
        matchStatement.getMatchStatements().forEach(st -> st.accept(this));
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        var returnInstruction = new ReturnInstruction();
        expressionAsInstruction = false;
        returnStatement.getExpression().accept(this);
        var exp = expressions.pop();
        expressionAsInstruction = true;
        returnInstruction.setValue(exp);
        scopedBlocks.peek().getInstructions().add(returnInstruction);
    }

    @Override
    public void visitVariableDeclarationStatement(VariableDeclarationStatement variableDeclarationStatement) {
        currentVariable = new Variable();
        currentVariable.setName(variableDeclarationStatement.getName());
        currentVariable.setMutable(variableDeclarationStatement.isMutable());
        variableType = true;
        variableDeclarationStatement.getType().accept(this);
        expressionAsInstruction = false;
        variableDeclarationStatement.getExpression().accept(this);
        expressionAsInstruction = true;
        var exp = expressions.pop();
        currentVariable.setValue(exp);
        var varDeclaration = new VarDeclaration();
        varDeclaration.setVariable(currentVariable);
        varDeclaration.setValue(exp);
        currentVariable = null;
        scopedBlocks.peek().getInstructions().add(varDeclaration);
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        scopedWhileInstructions.push(new WhileInstruction());
        expressionAsInstruction = false;
        whileStatement.getExpression().accept(this);
        var exp = expressions.pop();
        expressionAsInstruction = true;
        scopedWhileInstructions.peek().setCondition(exp);
        scopedBlocks.push(new Block());
        whileStatement.getStatements().forEach(st -> st.accept(this));
        var block = scopedBlocks.pop();
        scopedWhileInstructions.peek().setStatements(block);
        var whileInstruction = scopedWhileInstructions.pop();
        scopedBlocks.peek().getInstructions().add(whileInstruction);
    }

    @Override
    public void visitAddExpression(AddExpression addExpression) {
        var exp = new executor.ir.expressions.AddExpression();
        expressions.push(exp);
        addExpression.getLeftExpression().accept(this);
        addExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.AddExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitAndExpression(AndExpression andExpression) {
        var exp = new executor.ir.expressions.AndExpression();
        expressions.push(exp);
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.AndExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpression assignmentExpression) {
        var exp = new executor.ir.expressions.AssignmentExpression();
        expressions.push(exp);
        assignmentExpression.getLeftExpression().accept(this);
        assignmentExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        if (!(left instanceof executor.ir.expressions.Identifier)) {
            // TODO: throw exception tried assignning to non identifier
        }
        exp = (executor.ir.expressions.AssignmentExpression) expressions.pop();
        if (left instanceof executor.ir.expressions.Identifier id) {
            exp.setVariableName(id.getName());
            exp.setRightSide(right);
        }

        if (expressionAsInstruction) {
            var instructionExpression = new InstructionExpression();
            instructionExpression.setExpression(exp);
            scopedBlocks.peek().getInstructions().add(instructionExpression);
        } else {
            expressions.push(exp);
        }
    }

    @Override
    public void visitBaseExpression(BaseExpression baseExpression) {
        var exp = new executor.ir.expressions.BaseExpression();
        expressions.push(exp);
        baseExpression.getExpression().accept(this);
        var deeperExpression = expressions.pop();
        exp = (executor.ir.expressions.BaseExpression)expressions.pop();
        exp.setExpression(deeperExpression);
        expressions.push(exp);
    }

    @Override
    public void visitBooleanLiteralExpression(BooleanLiteralExpression booleanLiteralExpression) {
        var exp = new ConstExpression(new executor.ir.Type(false, "bool"), booleanLiteralExpression.getValue());
        expressions.push(exp);
    }

    @Override
    public void visitCompExpression(CompExpression compExpression) {
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
        expressions.push(exp);

    }

    @Override
    public void visitDivExpression(DivExpression divExpression) {
        var exp = new executor.ir.expressions.DivExpression();
        expressions.push(exp);
        divExpression.getLeftExpression().accept(this);
        divExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.DivExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitDivIntExpression(DivIntExpression divIntExpression) {
        divIntExpression.getLeftExpression().accept(this);
        divIntExpression.getRightExpression().accept(this);
    }

    @Override
    public void visitDoubleLiteralExpression(DoubleLiteralExpression doubleLiteralExpression) {
        var exp = new ConstExpression(new executor.ir.Type(false, "double"), doubleLiteralExpression.getValue());
        expressions.push(exp);
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression functionCallExpression) {
        var exp = new FunctionCall();
        exp.setName(functionCallExpression.getIdentifier());
        expressions.push(exp);
        functionCallExpression.getArgumentList().forEach(e -> e.accept(this));
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

    @Override
    public void visitIdentifier(Identifier identifier) {
        var exp = new executor.ir.expressions.Identifier(identifier.getName());
        expressions.push(exp);
    }

    @Override
    public void visitInsideMatchCompExpression(InsideMatchCompExpression insideMatchCompExpression) {
        
    }

    @Override
    public void visitInsideMatchTypeExpression(InsideMatchTypeExpression insideMatchTypeExpression) {
        
    }

    @Override
    public void visitIntegerLiteralExpression(IntegerLiteralExpression integerLiteralExpression) {
        var exp = new ConstExpression(new executor.ir.Type(false, "int"), integerLiteralExpression.getValue());
        expressions.push(exp);
    }

    @Override
    public void visitIsAsExpression(IsAsExpression isAsExpression) {
        if (isAsExpression.getOperator().getValue().equals("is")) {
            var exp = new IsExpression();
            expressions.push(exp);
            isAsExpression.getLeftExpression().accept(this);
            var deeperExp = expressions.pop();
            isAsType = true;
            isAsExpression.getType().accept(this);
            exp = (IsExpression) expressions.pop();
            exp.setExpression(deeperExp);
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
            expressions.push(exp);
        }
    }

    @Override
    public void visitModExpression(ModExpression modExpression) {
        var exp = new executor.ir.expressions.ModExpression();
        expressions.push(exp);
        modExpression.getLeftExpression().accept(this);
        modExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.ModExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitMulExpression(MulExpression mulExpression) {
        mulExpression.getLeftExpression().accept(this);
        mulExpression.getRightExpression().accept(this);
    }

    @Override
    public void visitNullCheckExpression(NullCheckExpression nullCheckExpression) {
        var exp = new executor.ir.expressions.NullCheckExpression();
        expressions.push(exp);
        nullCheckExpression.getLeftExpression().accept(this);
        nullCheckExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.NullCheckExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitNullLiteralExpression(NullLiteralExpression nullLiteralExpression) {
        var exp = new ConstExpression(null, null);
        expressions.push(exp);
    }

    @Override
    public void visitOrExpression(OrExpression orExpression) {
        var exp = new executor.ir.expressions.OrExpression();
        expressions.push(exp);
        orExpression.getLeftExpression().accept(this);
        orExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.OrExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitStringLiteralExpression(StringLiteralExpression stringLiteralExpression) {
        var exp = new ConstExpression(new executor.ir.Type(false, "string"), stringLiteralExpression.getValue());
        expressions.push(exp);
    }

    @Override
    public void visitSubExpression(SubExpression subExpression) {
        var exp = new executor.ir.expressions.SubExpression();
        expressions.push(exp);
        subExpression.getLeftExpression().accept(this);
        subExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        var left = expressions.pop();
        exp = (executor.ir.expressions.SubExpression) expressions.pop();
        exp.setLeftExpression(left);
        exp.setRightExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitUnaryExpression(UnaryExpression unaryExpression) {
        var exp = new executor.ir.expressions.UnaryExpression();
        exp.setUnaryOperator(unaryExpression.getOperator().getValue());
        expressions.push(exp);
        unaryExpression.getExpression().accept(this);
        var deeperExp = expressions.pop();
        exp = (executor.ir.expressions.UnaryExpression) expressions.pop();
        exp.setExpression(deeperExp);
        expressions.push(exp);
    }

    @Override
    public void visitType(Type type) {
        if (functionReturnType) {
            currentFunctionDef.setReturnType(new executor.ir.Type(type));
            functionReturnType = false;
        } else if (variableType) {
            currentVariable.setType(new executor.ir.Type(type));
            variableType = false;
        } else if (isAsType) {
            var exp = expressions.pop();
            if (exp instanceof IsExpression isExpression) {
                isExpression.setType(new executor.ir.Type(type));
                expressions.push(isExpression);
            } else if (exp instanceof AsExpression asExpression) {
                asExpression.setType(new executor.ir.Type(type));
                expressions.push(asExpression);
            }
            isAsType = false;
        }
    }

    @Override
    public void visitParameter(Parameter parameter) {
        currentVariable = new Variable();
        currentVariable.setMutable(parameter.isMutable());
        currentVariable.setName(parameter.getIdentifier());
        variableType = true;
        parameter.getType().accept(this);
        currentFunctionDef.getScope().addVariable(currentVariable);
        currentVariable = null;
    }
}
