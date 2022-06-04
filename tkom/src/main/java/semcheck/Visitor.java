package semcheck;

import parser.Parameter;
import parser.Program;
import parser.Type;
import parser.expressions.*;
import parser.statements.*;

public interface Visitor {

    void visitProgram(Program program);

    void visitFunctionDef(FunctionDef functionDef);

    void visitIfStatement(IfStatement ifStatement);

    void visitIfBlock(IfBlock ifBlock);

    void visitElseBlock(ElseBlock elseBlock);

    void visitInsideMatchStatement(InsideMatchStatement insideMatchStatement);

    void visitJumpLoopStatement(JumpLoopStatement jumpLoopStatement);

    void visitMatchStatement(MatchStatement matchStatement);

    void visitReturnStatement(ReturnStatement returnStatement);

    void visitVariableDeclarationStatement(VariableDeclarationStatement variableDeclarationStatement);

    void visitWhileStatement(WhileStatement whileStatement);

    void visitAddExpression(AddExpression addExpression);

    void visitAndExpression(AndExpression andExpression);

    void visitAssignmentExpression(AssignmentExpression assignmentExpression);

    void visitBaseExpression(BaseExpression baseExpression);

    void visitBooleanLiteralExpression(BooleanLiteralExpression booleanLiteralExpression);

    void visitCompExpression(CompExpression compExpression);

    void visitDivExpression(DivExpression divExpression);

    void visitDivIntExpression(DivIntExpression divIntExpression);

    void visitDoubleLiteralExpression(DoubleLiteralExpression doubleLiteralExpression);

    void visitFunctionCallExpression(FunctionCallExpression functionCallExpression);

    void visitIdentifier(Identifier identifier);

    void visitInsideMatchCompExpression(InsideMatchCompExpression insideMatchCompExpression);

    void visitInsideMatchTypeExpression(InsideMatchTypeExpression insideMatchTypeExpression);

    void visitIntegerLiteralExpression(IntegerLiteralExpression integerLiteralExpression);

    void visitIsAsExpression(IsAsExpression isAsExpression);

    void visitModExpression(ModExpression modExpression);

    void visitMulExpression(MulExpression mulExpression);

    void visitNullCheckExpression(NullCheckExpression nullCheckExpression);

    void visitNullLiteralExpression(NullLiteralExpression nullLiteralExpression);

    void visitOrExpression(OrExpression orExpression);

    void visitStringLiteralExpression(StringLiteralExpression stringLiteralExpression);

    void visitSubExpression(SubExpression subExpression);

    void visitUnaryExpression(UnaryExpression unaryExpression);

    void visitType(Type type);

    void visitParameter(Parameter parameter);
}
