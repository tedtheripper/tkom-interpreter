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

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried addition on null values");
        }

        if (!leftType.equals(rightType)) {
            throw new SemCheckException("Type mismatch for add operation");
        }

        if (leftType.getTypeName().equals("bool") || rightType.getTypeName().equals("bool")) {
            throw new SemCheckException("Addition is forbidden for bool type");
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

        if (!leftType.getTypeName().equals("bool") || !rightType.getTypeName().equals("bool")) {
            throw new SemCheckException("And operation is forbidden for any type besides bool");
        }

        return new Type(false, "bool");
    }

    @Override
    public Type visit(AsExpression asExpression, Scope scope) throws SemCheckException {
        var expType = asExpression.getExpression().evaluateType(this, scope);

        if (expType == null && !asExpression.getType().isNullable()) {
            throw new SemCheckException("Cannot wrap null in non-nullable type");
        }

        if (expType != null && expType.getTypeName().equals("double") && asExpression.getType().getTypeName().equals("bool")) {
            throw new SemCheckException("Casting from double to bool is illegal");
        }

        if (expType != null && expType.isNullable() && !asExpression.getType().isNullable()) {
            throw new SemCheckException("Cannot cast from nullable to non-nullable type, use ?? operator instead");
        }
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
    public Type visit(CompExpression compExpression, Scope scope) throws SemCheckException {
        var leftType = compExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = compExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried comparison on null values");
        }

        if (!leftType.getTypeName().equals(rightType.getTypeName())) {
            throw new SemCheckException("Type mismatch for comparison operation");
        }

        return new Type(false, "bool");
    }

    @Override
    public Type visit(DivExpression divExpression, Scope scope) throws SemCheckException {
        var leftType = divExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = divExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried division on null values");
        }

        if (leftType.getTypeName().equals("string") || rightType.getTypeName().equals("string")) {
            throw new SemCheckException("Tried division on string values");
        }

        if (leftType.getTypeName().equals("bool") || rightType.getTypeName().equals("bool")) {
            throw new SemCheckException("Tried division on bool values");
        }

        return new Type(false, "float");
    }

    @Override
    public Type visit(DivIntExpression divIntExpression, Scope scope) throws SemCheckException {
        var leftType = divIntExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = divIntExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried division on null values");
        }

        if (leftType.getTypeName().equals("string") || rightType.getTypeName().equals("string")) {
            throw new SemCheckException("Tried division on string values");
        }

        if (leftType.getTypeName().equals("bool") || rightType.getTypeName().equals("bool")) {
            throw new SemCheckException("Tried division on bool values");
        }

        return new Type(false, "int");
    }

    @Override
    public Type visit(Identifier identifier, Scope scope) {
        return scope.getVariable(identifier.getName()).getType();
    }

    @Override
    public Type visit(InsideMatchCompExpression insideMatchCompExpression, Scope scope) {
        // TODO:

        return new Type(false, "bool");
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
    public Type visit(ModExpression modExpression, Scope scope) throws SemCheckException {
        var leftType = modExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = modExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried modulo op on null value");
        }

        if (!leftType.getTypeName().equals("int") || !rightType.getTypeName().equals("int")) {
            throw new SemCheckException("Modulo operation is illegal with types other than int");
        }

        return new Type(false, "int");
    }

    @Override
    public Type visit(MulExpression mulExpression, Scope scope) {
        return null; // TODO;
    }

    @Override
    public Type visit(NullCheckExpression nullCheckExpression, Scope scope) {
        return null; // TODO;
    }

    @Override
    public Type visit(OrExpression orExpression, Scope scope) {
        return new Type(false, "bool");
    }

    @Override
    public Type visit(SubExpression subExpression, Scope scope) {
        return null; // TODO:
    }

    @Override
    public Type visit(UnaryExpression unaryExpression, Scope scope) throws SemCheckException {
        return unaryExpression.getExpression().evaluateType(this, scope);
    }

    @Override
    public Type visit(ConstExpression constExpression, Scope scope) {
        return constExpression.getType();
    }

    @Override
    public Type visit(FunctionCall functionCall, Scope scope) throws SemCheckException {
        var hasFunction = scope.hasFunction(functionCall.getName());

        if (!hasFunction) {
            throw new SemCheckException("Tried calling function that is not defined");
        }

        var function = scope.getFunction(functionCall.getName());

        return function.getReturnType();
    }
}
