package semcheck;

import executor.Visitor;
import parser.Parameter;
import parser.Program;
import parser.Type;
import parser.expressions.*;
import parser.statements.*;
import semcheck.exception.SemCheckException;

public interface BuildVisitor extends Visitor {

    void visitProgram(Program program) throws SemCheckException;

    void visitFunctionDef(FunctionDef functionDef) throws SemCheckException;

    void visitIfStatement(IfStatement ifStatement) throws SemCheckException;

    void visitIfBlock(IfBlock ifBlock) throws SemCheckException;

    void visitElseBlock(ElseBlock elseBlock) throws SemCheckException;

    void visitInsideMatchStatement(InsideMatchStatement insideMatchStatement) throws SemCheckException;

    void visitJumpLoopStatement(JumpLoopStatement jumpLoopStatement) throws SemCheckException;

    void visitMatchStatement(MatchStatement matchStatement) throws SemCheckException;

    void visitReturnStatement(ReturnStatement returnStatement) throws SemCheckException;

    void visitVariableDeclarationStatement(VariableDeclarationStatement variableDeclarationStatement) throws SemCheckException;

    void visitWhileStatement(WhileStatement whileStatement) throws SemCheckException;

    void visitAddExpression(AddExpression addExpression) throws SemCheckException;

    void visitAndExpression(AndExpression andExpression) throws SemCheckException;

    void visitAssignmentExpression(AssignmentExpression assignmentExpression) throws SemCheckException;

    void visitBaseExpression(BaseExpression baseExpression) throws SemCheckException;

    void visitBooleanLiteralExpression(BooleanLiteralExpression booleanLiteralExpression) throws SemCheckException;

    void visitCompExpression(CompExpression compExpression) throws SemCheckException;

    void visitDivExpression(DivExpression divExpression) throws SemCheckException;

    void visitDivIntExpression(DivIntExpression divIntExpression) throws SemCheckException;

    void visitDoubleLiteralExpression(DoubleLiteralExpression doubleLiteralExpression) throws SemCheckException;

    void visitFunctionCallExpression(FunctionCallExpression functionCallExpression) throws SemCheckException;

    void visitIdentifier(Identifier identifier) throws SemCheckException;

    void visitInsideMatchCompExpression(InsideMatchCompExpression insideMatchCompExpression) throws SemCheckException;

    void visitInsideMatchTypeExpression(InsideMatchTypeExpression insideMatchTypeExpression) throws SemCheckException;

    void visitIntegerLiteralExpression(IntegerLiteralExpression integerLiteralExpression) throws SemCheckException;

    void visitIsAsExpression(IsAsExpression isAsExpression) throws SemCheckException;

    void visitModExpression(ModExpression modExpression) throws SemCheckException;

    void visitMulExpression(MulExpression mulExpression) throws SemCheckException;

    void visitNullCheckExpression(NullCheckExpression nullCheckExpression) throws SemCheckException;

    void visitNullLiteralExpression(NullLiteralExpression nullLiteralExpression) throws SemCheckException;

    void visitOrExpression(OrExpression orExpression) throws SemCheckException;

    void visitStringLiteralExpression(StringLiteralExpression stringLiteralExpression) throws SemCheckException;

    void visitSubExpression(SubExpression subExpression) throws SemCheckException;

    void visitUnaryExpression(UnaryExpression unaryExpression) throws SemCheckException;

    void visitType(Type type);

    void visitParameter(Parameter parameter);
}
