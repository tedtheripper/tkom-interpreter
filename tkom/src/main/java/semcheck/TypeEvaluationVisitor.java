package semcheck;

import executor.ir.Expression;
import executor.ir.Scope;
import executor.ir.Type;
import executor.ir.Variable;
import executor.ir.expressions.*;
import semcheck.exception.SemCheckException;

import java.util.List;
import java.util.Map;

public class TypeEvaluationVisitor implements TypeVisitor {

    private static final String INT_TYPE_NAME = "int";
    private static final String STRING_TYPE_NAME = "string";
    private static final String DOUBLE_TYPE_NAME = "double";
    private static final String BOOL_TYPE_NAME = "bool";

    public TypeEvaluationVisitor() {
        //
    }

    @Override
    public Type visit(AddExpression addExpression, Scope scope) throws SemCheckException {
        var leftType = addExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = addExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried ADD on null values");
        }

        if (!leftType.equals(rightType)) {
            throw new SemCheckException("Type mismatch for ADD operation");
        }

        if (leftType.getTypeName().equals(BOOL_TYPE_NAME) || rightType.getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("ADD is forbidden for bool type");
        }

        return new Type(false, leftType.getTypeName());
    }

    @Override
    public Type visit(AndExpression andExpression, Scope scope) throws SemCheckException {
        var leftType = andExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = andExpression.getRightExpression().evaluateType(this, scope);

        if (!leftType.equals(rightType)) {
            throw new SemCheckException("Type mismatch for AND operation");
        }

        if (leftType.isNullable() || rightType.isNullable()) {
            throw new SemCheckException("AND operation is forbidden for nullable types");
        }

        if (!leftType.getTypeName().equals(BOOL_TYPE_NAME) || !rightType.getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("AND operation is forbidden for any type besides bool");
        }

        return new Type(false, BOOL_TYPE_NAME);
    }

    @Override
    public Type visit(AsExpression asExpression, Scope scope) throws SemCheckException {
        var expType = asExpression.getExpression().evaluateType(this, scope);

        if (expType == null && !asExpression.getType().isNullable()) {
            throw new SemCheckException("Cannot wrap null in non-nullable type");
        }

        if (expType != null && expType.getTypeName().equals(DOUBLE_TYPE_NAME) && asExpression.getType().getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("Cannot cast from double to bool");
        }

        return asExpression.getType();
    }

    @Override
    public Type visit(AssignmentExpression assignmentExpression, Scope scope) throws SemCheckException {
        var variableTypeName = scope.getVariable(assignmentExpression.getVariableName()).getType().getTypeName();
        var rightType = assignmentExpression.getRightSide().evaluateType(this, scope);

        if (rightType != null && !variableTypeName.equals(rightType.getTypeName())) {
            throw new SemCheckException("Type mismatch for ASSIGNMENT operation");
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
            throw new SemCheckException("Tried COMPARING null values");
        }

        if (leftType.isNullable() || rightType.isNullable()) {
            throw new SemCheckException("COMPARISON is illegal with nullable types");
        }

        if (leftType.getTypeName().equals(BOOL_TYPE_NAME) || rightType.getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("Illegal type for COMPARISON operation: bool");
        }


        if (!leftType.getTypeName().equals(rightType.getTypeName())) {
            throw new SemCheckException("Type mismatch for COMPARISON operation");
        }

        return new Type(false, BOOL_TYPE_NAME);
    }

    @Override
    public Type visit(DivExpression divExpression, Scope scope) throws SemCheckException {
        var leftType = divExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = divExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried DIV on null values");
        }

        if (leftType.isNullable() || rightType.isNullable()) {
            throw new SemCheckException("DIV operation is illegal with nullable types");
        }

        if (leftType.getTypeName().equals(STRING_TYPE_NAME) || rightType.getTypeName().equals(STRING_TYPE_NAME)) {
            throw new SemCheckException("Tried DIV on string values");
        }

        if (leftType.getTypeName().equals(BOOL_TYPE_NAME) || rightType.getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("Tried DIV on bool values");
        }

        if (!leftType.getTypeName().equals(rightType.getTypeName())) {
            throw new SemCheckException("Type mismatch in DIV operation");
        }

        return new Type(false, DOUBLE_TYPE_NAME);
    }

    @Override
    public Type visit(DivIntExpression divIntExpression, Scope scope) throws SemCheckException {
        var leftType = divIntExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = divIntExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried DIVINT on null values");
        }

        if (leftType.isNullable() || rightType.isNullable()) {
            throw new SemCheckException("DIVINT operation is illegal with nullable types");
        }

        if (leftType.getTypeName().equals(STRING_TYPE_NAME) || rightType.getTypeName().equals(STRING_TYPE_NAME)) {
            throw new SemCheckException("Tried DIVINT on string values");
        }

        if (leftType.getTypeName().equals(BOOL_TYPE_NAME) || rightType.getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("Tried DIVINT on bool values");
        }

        if (!leftType.getTypeName().equals(rightType.getTypeName())) {
            throw new SemCheckException("Type mismatch in DIV operation");
        }

        return new Type(false, leftType.getTypeName());
    }

    @Override
    public Type visit(Identifier identifier, Scope scope) {
        return scope.getVariable(identifier.getName()).getType();
    }

    @Override
    public Type visit(InsideMatchCompExpression insideMatchCompExpression, Scope scope) throws SemCheckException {
        var expType = insideMatchCompExpression.getExpression().evaluateType(this, scope);

        if (expType == null) {
            throw new SemCheckException("Match comparison does not allow null values, use `is null` instead");
        }

        if (scope.getVariable("_").getType().isNullable() || expType.isNullable()) {
            throw new SemCheckException("Match comparison is illegal with nullable types");
        }

        if (!scope.getVariable("_").getType().getTypeName().equals(expType.getTypeName())) {
            throw new SemCheckException("Match comparison type mismatch");
        }

        return new Type(false, BOOL_TYPE_NAME);
    }

    @Override
    public Type visit(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope) {
        return new Type(false, BOOL_TYPE_NAME);
    }

    @Override
    public Type visit(IsExpression isExpression, Scope scope) {
        return new Type(false, BOOL_TYPE_NAME);
    }

    @Override
    public Type visit(ModExpression modExpression, Scope scope) throws SemCheckException {
        var leftType = modExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = modExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried MOD on null value");
        }

        if (!leftType.getTypeName().equals(INT_TYPE_NAME) || !rightType.getTypeName().equals(INT_TYPE_NAME)) {
            throw new SemCheckException("MOD operation is illegal with types other than int");
        }

        return new Type(false, INT_TYPE_NAME);
    }

    @Override
    public Type visit(MulExpression mulExpression, Scope scope) throws SemCheckException {
        var leftType = mulExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = mulExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried MUL on null value");
        }

        if (leftType.getTypeName().equals(BOOL_TYPE_NAME) || rightType.getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("MUL operation is illegal with bool type");
        }

        if (leftType.getTypeName().equals(STRING_TYPE_NAME) || rightType.getTypeName().equals(STRING_TYPE_NAME)) {
            throw new SemCheckException("MUL operation is illegal with string type");
        }

        if (leftType.isNullable() || rightType.isNullable()) {
            throw new SemCheckException("MUL operation is illegal with nullable types");
        }

        return checkCombinationsMathOperations(leftType, rightType);

    }

    @Override
    public Type visit(NullCheckExpression nullCheckExpression, Scope scope) throws SemCheckException {
        var leftType = nullCheckExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = nullCheckExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried NULL_CHECK on null value");
        }

        if (!leftType.isNullable()) {
            throw new SemCheckException("NULL_CHECK operation is illegal with non-nullable type on the left side");
        }

        if (!leftType.getTypeName().equals(rightType.getTypeName())) {
            throw new SemCheckException("Type mismatch in NULL_CHECK operation");
        }


        return new Type(false, leftType.getTypeName());
    }

    @Override
    public Type visit(OrExpression orExpression, Scope scope) throws SemCheckException {
        var leftType = orExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = orExpression.getRightExpression().evaluateType(this, scope);

        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried OR on null value");
        }

        if (!leftType.equals(rightType)) {
            throw new SemCheckException("Type mismatch for OR operation");
        }

        if (leftType.isNullable() || rightType.isNullable()) {
            throw new SemCheckException("OR operation is forbidden for nullable types");
        }

        if (!leftType.getTypeName().equals(BOOL_TYPE_NAME) || !rightType.getTypeName().equals(BOOL_TYPE_NAME)) {
            throw new SemCheckException("OR operation is forbidden for any type besides bool");
        }

        return new Type(false, BOOL_TYPE_NAME);
    }

    @Override
    public Type visit(SubExpression subExpression, Scope scope) throws SemCheckException {
        var leftType = subExpression.getLeftExpression().evaluateType(this, scope);
        var rightType = subExpression.getRightExpression().evaluateType(this, scope);
        if (leftType == null || rightType == null) {
            throw new SemCheckException("Tried SUB on null value");
        }

        if (!leftType.equals(rightType)) {
            throw new SemCheckException("Type mismatch for SUB operation");
        }

        if (leftType.isNullable() || rightType.isNullable()) {
            throw new SemCheckException("SUB operation is forbidden for nullable types");
        }

        return checkCombinationsMathOperations(leftType, rightType);
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

        var functionParams = function.getScope().getDeclaredVariables();
        var paramOrder = function.getScope().getVariablesOrder();

        var arguments = functionCall.getArguments();

        checkArguments(function.getName(), functionParams, paramOrder, arguments, scope);
        return function.getReturnType();
    }

    @Override
    public Type visit(LibFunctionCall libFunctionCall, Scope scope) throws SemCheckException {
        var hasFunction = scope.hasFunction(libFunctionCall.getName());

        if (!hasFunction) {
            throw new SemCheckException("Tried calling function that is not defined");
        }

        var function = scope.getFunction(libFunctionCall.getName());

        var functionParams = function.getScope().getDeclaredVariables();
        var paramOrder = function.getScope().getVariablesOrder();

        var arguments = libFunctionCall.getArguments();

        checkArguments(function.getName(), functionParams, paramOrder, arguments, scope);
        return function.getReturnType();
    }

    private void checkArguments(String functionName, Map<String, Variable> paramDefinitions, List<String> paramOrder, List<Expression> arguments, Scope scope) throws SemCheckException {
        if (arguments.size() != paramOrder.size()) {
            throw new SemCheckException(String.format("Arguments for function: %s do not match function definition", functionName));
        }

        int position = 0;
        for (var p : paramOrder) {
            var param = paramDefinitions.get(p);
            var exp = arguments.get(position);
            var expType = exp.evaluateType(this, scope);
            if (!expType.getTypeName().equals(param.getType().getTypeName())) {
                throw new SemCheckException(String.format("Argument type mismatch in call of function: %s, param: %s", functionName, param.getName()));
            } else {
                if (expType.isNullable() && !param.getType().isNullable()) {
                    throw new SemCheckException(String.format("Argument optionality mismatch in call of function: %s, param: %s", functionName, param.getName()));
                }
            }
            position++;
        }
    }

    private Type checkCombinationsMathOperations(Type leftType, Type rightType) {
        if (leftType.getTypeName().equals(INT_TYPE_NAME) && rightType.getTypeName().equals(INT_TYPE_NAME)) return new Type(false, INT_TYPE_NAME);
        if (leftType.getTypeName().equals(INT_TYPE_NAME) && rightType.getTypeName().equals(DOUBLE_TYPE_NAME)) return new Type(false, DOUBLE_TYPE_NAME);
        if (leftType.getTypeName().equals(DOUBLE_TYPE_NAME) && rightType.getTypeName().equals(INT_TYPE_NAME)) return new Type(false, DOUBLE_TYPE_NAME);
        if (leftType.getTypeName().equals(DOUBLE_TYPE_NAME) && rightType.getTypeName().equals(DOUBLE_TYPE_NAME)) return new Type(false, DOUBLE_TYPE_NAME);
        return null;
    }
}
