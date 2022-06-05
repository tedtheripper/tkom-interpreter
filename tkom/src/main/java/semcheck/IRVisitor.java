package semcheck;

import executor.ir.Expression;
import executor.ir.*;
import executor.ir.expressions.AsExpression;
import executor.ir.expressions.ConstExpression;
import executor.ir.expressions.FunctionCall;
import executor.ir.expressions.IsExpression;
import executor.ir.instructions.*;
import parser.Parameter;
import parser.Program;
import parser.Type;
import parser.expressions.*;
import parser.statements.*;
import semcheck.exception.SemCheckException;

import java.util.*;

public class IRVisitor implements Visitor {

    private GlobalBlock globalBlock;
    private Function currentFunctionDef;
    private final Stack<IfInstruction> scopedIfInstructions = new Stack<>();
    private final Stack<WhileInstruction> scopedWhileInstructions = new Stack<>();
    private final Stack<Block> scopedBlocks = new Stack<>();
    private Variable currentVariable;
    private final Stack<Expression> expressions = new Stack<>();
    private MatchInstruction currentMatchInstruction;

    private boolean expressionAsInstruction = true;

    private boolean functionReturnType = false;
    private boolean variableType = false;
    private boolean isAsType = false;
    private boolean insideMatchType = false;

    public IRVisitor() {
        //
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
        currentFunctionDef = new Function();
        currentFunctionDef.setName(functionDef.getName());
        currentFunctionDef.setScope(new Scope());
        currentFunctionDef.getScope().setUpperScope(globalBlock.getGlobalScope());
        functionDef.getParameterList().forEach(param -> param.accept(this));
        functionReturnType = true;
        functionDef.getFunctionReturnType().accept(this);
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(currentFunctionDef.getScope());
        scopedBlocks.push(newBlock);
        for (var st : functionDef.getStatementsBlock()) {
            st.accept(this);
        }
        var block = scopedBlocks.pop();
        currentFunctionDef.setInstructions(block);
        globalBlock.getFunctions().put(currentFunctionDef.getName(), currentFunctionDef);
        currentFunctionDef = null;
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
        currentInsideMatchInstruction.setDefault(insideMatchStatement.isDefault());
        if (!insideMatchStatement.isDefault()) {
            expressionAsInstruction = false;
            insideMatchStatement.getExpression().accept(this);
            expressionAsInstruction = true;
            var exp = expressions.pop();
            currentInsideMatchInstruction.setExpression(exp);
        }
        var newBlock = new Block();
        newBlock.getScope().setUpperScope(scopedBlocks.peek().getScope());
        scopedBlocks.push(newBlock);
        insideMatchStatement.getSimpleStatement().accept(this);
        var block = scopedBlocks.pop();
        if (block.getInstructions().size() != 1) {
            throw new SemCheckException("More than one statement inside match option");
        }
        currentInsideMatchInstruction.setInstruction(block.getInstructions().get(0));
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
        currentMatchInstruction.setScope(new Scope(scopedBlocks.peek().getScope().getUpperScope()));
        currentMatchInstruction.setMatchStatements(new ArrayList<>());
        expressionAsInstruction = false;
        matchStatement.getExpression().accept(this);
        expressionAsInstruction = true;
        var exp = expressions.pop();
        currentMatchInstruction.setExpression(exp);
        currentMatchInstruction.getScope().addVariable(
                new Variable("_", null, false, exp)
        );
        for(var st : matchStatement.getMatchStatements()) {
            st.accept(this);
        }
        scopedBlocks.peek().getInstructions().add(currentMatchInstruction);
        currentMatchInstruction = null;
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) throws SemCheckException {
        if (currentFunctionDef == null) {
            throw new SemCheckException("Found return outside of function");
        }
        var returnInstruction = new ReturnInstruction();
        expressionAsInstruction = false;
        returnStatement.getExpression().accept(this);
        var exp = expressions.pop();
        expressionAsInstruction = true;
        returnInstruction.setValue(exp);
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
        currentVariable.setValue(exp);
        var varDeclaration = new VarDeclaration();
        varDeclaration.setVariable(currentVariable);
        varDeclaration.setValue(exp);
        scopedBlocks.peek().getScope().addVariable(currentVariable);
        scopedBlocks.peek().getInstructions().add(varDeclaration);
        currentVariable = null;
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) throws SemCheckException {
        scopedWhileInstructions.push(new WhileInstruction());
        expressionAsInstruction = false;
        whileStatement.getExpression().accept(this);
        var exp = expressions.pop();
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
        expressions.push(exp);
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpression assignmentExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.AssignmentExpression();
        expressions.push(exp);
        expressionAsInstruction = false;
        assignmentExpression.getLeftExpression().accept(this);
        assignmentExpression.getRightExpression().accept(this);
        expressionAsInstruction = true;
        var right = expressions.pop();
        var left = expressions.pop();
        if (!(left instanceof executor.ir.expressions.Identifier)) {
            throw new SemCheckException("Tried to assign value to non-identifier");
        }
        exp = (executor.ir.expressions.AssignmentExpression) expressions.pop();
        if (left instanceof executor.ir.expressions.Identifier id) {
            exp.setVariableName(id.getName());
            exp.setRightSide(right);
        }

        if (!scopedBlocks.peek().getScope().hasVariable(exp.getVariableName())) {
            throw new SemCheckException(String.format("Variable %s has not been declared", exp.getVariableName()));
        } else {
            var variable = scopedBlocks.peek().getScope().getVariable(exp.getVariableName());
            if (!variable.isMutable()) {
                throw new SemCheckException("Tried changing value of constant variable");
            }
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
    public void visitBaseExpression(BaseExpression baseExpression) throws SemCheckException {
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
        expressions.push(exp);
    }

    @Override
    public void visitDoubleLiteralExpression(DoubleLiteralExpression doubleLiteralExpression) {
        var exp = new ConstExpression(new executor.ir.Type(false, "double"), doubleLiteralExpression.getValue());
        expressions.push(exp);
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression functionCallExpression) throws SemCheckException {
        var exp = new FunctionCall();
        exp.setName(functionCallExpression.getIdentifier());
        expressions.push(exp);
        expressionAsInstruction = false;
        for(var e : functionCallExpression.getArgumentList()) {
            e.accept(this);
        }
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
    public void visitInsideMatchCompExpression(InsideMatchCompExpression insideMatchCompExpression) throws SemCheckException {
        var exp = new executor.ir.expressions.InsideMatchCompExpression();
        exp.setOperator(insideMatchCompExpression.getOperator().getValue());
        expressions.push(exp);
        insideMatchCompExpression.getRightExpression().accept(this);
        var right = expressions.pop();
        exp = (executor.ir.expressions.InsideMatchCompExpression) expressions.pop();
        exp.setExpression(right);
        expressions.push(exp);
    }

    @Override
    public void visitInsideMatchTypeExpression(InsideMatchTypeExpression insideMatchTypeExpression) {
        var exp = new executor.ir.expressions.InsideMatchTypeExpression();
        expressions.push(exp);
        insideMatchType = true;
        insideMatchTypeExpression.getType().accept(this);
//        exp = (executor.ir.expressions.InsideMatchCompExpression) expressions.pop();
//        currentMatchInstruction.s
    }

    @Override
    public void visitIntegerLiteralExpression(IntegerLiteralExpression integerLiteralExpression) {
        var exp = new ConstExpression(new executor.ir.Type(false, "int"), integerLiteralExpression.getValue());
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
        expressions.push(exp);
    }

    @Override
    public void visitNullLiteralExpression(NullLiteralExpression nullLiteralExpression) {
        var exp = new ConstExpression(null, null);
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
        expressions.push(exp);
    }

    @Override
    public void visitStringLiteralExpression(StringLiteralExpression stringLiteralExpression) {
        var exp = new ConstExpression(new executor.ir.Type(false, "string"), stringLiteralExpression.getValue());
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
        } else if (insideMatchType) {
            var exp = (executor.ir.expressions.InsideMatchTypeExpression)expressions.pop();
            exp.setType(new executor.ir.Type(type));
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
        currentFunctionDef.getScope().addVariable(currentVariable);
        currentVariable = null;
    }
}
