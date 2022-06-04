package semcheck;

import executor.ir.Scope;
import executor.ir.Type;
import executor.ir.expressions.*;
import semcheck.exception.SemCheckException;

public class TypeEvaluationVisitor implements TypeVisitor {


    @Override
    public Type visit(AddExpression addExpression, Scope scope) throws SemCheckException {
        var leftType = addExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = addExpression.getRightExpression().evaluateType(this, scope);

        if (!leftType.equals(rightType)) {
            throw new SemCheckException("Type mismatch for add operation");
        }

        return new Type(false, leftType.getTypeName());
    }

    @Override
    public Type visit(AndExpression andExpression, Scope scope) throws SemCheckException {
        var leftType = andExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = andExpression.getRightExpression().evaluateType(this, scope);

        if (!leftType.equals(rightType)) {
            throw new SemCheckException("Type mismatch for and operation");
        }

        return new Type(false, "bool");
    }

    @Override
    public Type visit(AsExpression asExpression, Scope scope) {
        return asExpression.getType();
    }

    @Override
    public Type visit(AssignmentExpression assignmentExpression, Scope scope) throws SemCheckException {
        var variableTypeName = scope.getVariable(assignmentExpression.getVariableName()).getType().getTypeName();
        var rightType = assignmentExpression.getRightSide().evaluateType(this, scope).getTypeName();

        if (!variableTypeName.equals(rightType)) {
            throw new SemCheckException("Type mismatch for assignment operation");
        }

        return assignmentExpression.getRightSide().evaluateType(this, scope);
    }

    @Override
    public Type visit(BaseExpression baseExpression, Scope scope) throws SemCheckException {
        return baseExpression.getExpression().evaluateType(this, scope);
    }

    @Override
    public Type visit(CompExpression compExpression, Scope scope) {
        return new Type(false, "bool");
    }

    @Override
    public Type visit(DivExpression divExpression, Scope scope) {
        return null;
    }

    @Override
    public Type visit(DivIntExpression divIntExpression, Scope scope) {
        return null;
    }

    @Override
    public Type visit(Identifier identifier, Scope scope) {
        return scope.getVariable(identifier.getName()).getType();
    }

    @Override
    public Type visit(InsideMatchCompExpression insideMatchCompExpression, Scope scope) {
        return null;
    }

    @Override
    public Type visit(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope) {
        return new Type(false, "bool");
    }

    @Override
    public Type visit(IsExpression isExpression, Scope scope) {
        return new Type(false, "bool");
    }

    @Override
    public Type visit(ModExpression modExpression, Scope scope) {
        return null;
    }

    @Override
    public Type visit(MulExpression mulExpression, Scope scope) {
        return null;
    }

    @Override
    public Type visit(NullCheckExpression nullCheckExpression, Scope scope) {
        return null;
    }

    @Override
    public Type visit(OrExpression orExpression, Scope scope) {
        return new Type(false, "bool");
    }

    @Override
    public Type visit(SubExpression subExpression, Scope scope) {
        return null;
    }

    @Override
    public Type visit(UnaryExpression unaryExpression, Scope scope) throws SemCheckException {
        return unaryExpression.getExpression().evaluateType(this, scope);
    }

    @Override
    public Type visit(ConstExpression constExpression, Scope scope) {
        return constExpression.getType();
    }
}
