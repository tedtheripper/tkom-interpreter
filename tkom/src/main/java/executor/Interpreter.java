package executor;

import executor.exceptions.CastException;
import executor.exceptions.RuntimeException;
import executor.exceptions.StackOverflowException;
import executor.ir.*;
import executor.ir.expressions.*;
import executor.ir.instructions.*;
import executor.stdlib.StdLibImpl;

import java.util.*;

public class Interpreter implements Executor {

    private static final String INT_TYPE_NAME = "int";
    private static final String STRING_TYPE_NAME = "string";
    private static final String DOUBLE_TYPE_NAME = "double";
    private static final String BOOL_TYPE_NAME = "bool";

    private static final int MAX_STACK_SIZE = 16;

    private boolean continueDetected = false;
    private boolean breakDetected = false;
    private boolean returnDetected = false;
    private boolean matchFinished = false;

    private final StdLibImpl stdLib;
    private final GlobalBlock global;

    private final Stack<ExecutorObject> objects = new Stack<>();
    private final Stack<FunctionCallContext> callStack = new Stack<>();

    public Interpreter(GlobalBlock global) {
        this.stdLib = new StdLibImpl();
        this.global = global;
    }

    public void run() {
        try {
            global.execute(this, global.getGlobalScope());
        } catch (Exception e) {
            System.out.println("Internal error occurred");
            var stackTrace = e.getStackTrace();
            Arrays.stream(stackTrace).forEach(System.out::println);
            System.out.println(e.getMessage());
        }
    }

    public void runNoisy() throws RuntimeException {
        global.execute(this, global.getGlobalScope());
    }

    @Override
    public void execute(GlobalBlock globalBlock, Scope scope) throws RuntimeException {
        for(var instruction : globalBlock.getInstructions()) {
            instruction.execute(this, scope);
        }
    }

    @Override
    public void execute(Block block, Scope scope) throws RuntimeException {
        for(var instruction : block.getInstructions()) {
            if (breakDetected || continueDetected || (!callStack.isEmpty() && callStack.peek().isReturnDetected())) {
                break;
            }
            instruction.execute(this, scope);
        }
    }

    @Override
    public void execute(AddExpression addExpression, Scope scope) throws RuntimeException {
        addExpression.getLeftExpression().execute(this, scope);
        addExpression.getRightExpression().execute(this, scope);
        var obj = objects.peek();
        if (obj instanceof IntegerObject) {
            var rightValue = ((IntegerObject)objects.pop()).getValue();
            var leftValue = ((IntegerObject)objects.pop()).getValue();
            objects.push(new IntegerObject(leftValue + rightValue));
        } else if (obj instanceof DoubleObject){
            var rightValue = ((DoubleObject)objects.pop()).getValue();
            var leftValue = ((DoubleObject)objects.pop()).getValue();
            objects.push(new DoubleObject(leftValue + rightValue));
        } else {
            var rightValue = ((StringObject)objects.pop()).getValue();
            var leftValue = ((StringObject)objects.pop()).getValue();
            objects.push(new StringObject(leftValue + rightValue));
        }
    }

    @Override
    public void execute(AndExpression andExpression, Scope scope) throws RuntimeException {
        andExpression.getLeftExpression().execute(this, scope);
        var leftValue = ((BooleanObject)objects.pop()).isValue();
        if (!leftValue) {
            objects.push(new BooleanObject(false));
            return;
        }
        andExpression.getRightExpression().execute(this, scope);
        var rightValue = ((BooleanObject)objects.pop()).isValue();
        objects.push(new BooleanObject(rightValue));
    }

    @Override
    public void execute(AsExpression asExpression, Scope scope) throws RuntimeException {
        asExpression.getExpression().execute(this, scope);
        var exp = objects.pop();
        var type = asExpression.getType();
        if (type == null) {
            objects.push(new NullObject());
            return;
        }
        tryCastObject(exp, type);
    }

    @Override
    public void execute(AssignmentExpression assignmentExpression, Scope scope) throws RuntimeException {
        assignmentExpression.getRightSide().execute(this, scope);
        scope.getVariable(assignmentExpression.getVariableName()).setObject(objects.peek());
    }

    @Override
    public void execute(BaseExpression baseExpression, Scope scope) throws RuntimeException {
        baseExpression.getExpression().execute(this, scope);
    }

    @Override
    public void execute(CompExpression compExpression, Scope scope) throws RuntimeException {
        compExpression.getLeftExpression().execute(this, scope);
        compExpression.getRightExpression().execute(this, scope);
        var operator = compExpression.getOperator();
        if (objects.peek() instanceof IntegerObject) {
            var rightValue = (IntegerObject)objects.pop();
            var leftValue = (IntegerObject)objects.pop();
            var res = leftValue.compareTo(rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        } else if (objects.peek() instanceof DoubleObject) {
            var rightValue = (DoubleObject)objects.pop();
            var leftValue = (DoubleObject)objects.pop();
            var res = leftValue.compareTo(rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        } else if (objects.peek() instanceof BooleanObject) {
            var rightValue = (BooleanObject)objects.pop();
            var leftValue = (BooleanObject)objects.pop();
            var res = leftValue.compareTo(rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        } else if (objects.peek() instanceof StringObject) {
            var rightValue = (StringObject)objects.pop();
            var leftValue = (StringObject)objects.pop();
            var res = leftValue.compareTo(rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        }
    }

    @Override
    public void execute(ConstExpression constExpression, Scope scope) throws RuntimeException {
        pushByType(constExpression.getType(), constExpression.getValue());
    }

    @Override
    public void execute(DivExpression divExpression, Scope scope) throws RuntimeException {
        divExpression.getLeftExpression().execute(this, scope);
        divExpression.getRightExpression().execute(this, scope);
        var obj = objects.peek();
        if (obj instanceof IntegerObject) {
            var rightValue = ((IntegerObject)objects.pop()).getValue();
            var leftValue = ((IntegerObject)objects.pop()).getValue();
            objects.push(new DoubleObject((double)leftValue / (double)rightValue));
        } else {
            var rightValue = ((DoubleObject)objects.pop()).getValue();
            var leftValue = ((DoubleObject)objects.pop()).getValue();
            objects.push(new DoubleObject(leftValue / rightValue));
        }
    }

    @Override
    public void execute(DivIntExpression divIntExpression, Scope scope) throws RuntimeException {
        divIntExpression.getLeftExpression().execute(this, scope);
        divIntExpression.getRightExpression().execute(this, scope);
        var obj = objects.peek();
        if (obj instanceof IntegerObject) {
            var rightValue = ((IntegerObject)objects.pop()).getValue();
            var leftValue = ((IntegerObject)objects.pop()).getValue();
            objects.push(new IntegerObject(leftValue / rightValue));
        } else {
            var rightValue = ((DoubleObject)objects.pop()).getValue();
            var leftValue = ((DoubleObject)objects.pop()).getValue();
            objects.push(new DoubleObject((int)(leftValue / rightValue)));
        }
    }

    @Override
    public void execute(FunctionCall functionCall, Scope scope) throws RuntimeException {
        var functionDef = global.getGlobalScope().getFunction(functionCall.getName());
        List<ExecutorObject> argumentValues = new ArrayList<>();
        var argCount = 0;
        for(var arg : functionCall.getArguments()) {
            arg.execute(this, scope);
            argCount++;
        }
        while (argCount > 0) {
            argumentValues.add(objects.pop());
            argCount--;
        }
        Collections.reverse(argumentValues);
        var funDef = (UserFunction)global.getFunctions().get(functionCall.getName());
        var callContext = new FunctionCallContext(functionDef.getScope().getVariablesOrder(), argumentValues, funDef.getScope(), global.getFunctions().get(functionCall.getName()));
        if (callStack.size() == MAX_STACK_SIZE) {
            throw new StackOverflowException(String.format("Stack size: %d exceeded", MAX_STACK_SIZE));
        }
        callStack.push(callContext);
        funDef.getInstructions().execute(this, callContext.getScope());
        var obj = callStack.peek().getReturnValue();
        callStack.pop();
        returnDetected = false;
        objects.push(obj);

    }

    @Override
    public void execute(Identifier identifier, Scope scope) throws RuntimeException {
        var variable = scope.getVariable(identifier.getName());
        var obj = variable.getObject();
        if (obj instanceof IntegerObject leftValue) {
            pushByType(variable.getType(), leftValue.getValue());
        } else if (obj instanceof DoubleObject leftValue) {
            pushByType(variable.getType(), leftValue.getValue());
        } else if (obj instanceof BooleanObject leftValue) {
            pushByType(variable.getType(), leftValue.isValue());
        } else if (obj instanceof StringObject leftValue) {
            pushByType(variable.getType(), leftValue.getValue());
        } else {
            pushByType(null, null);
        }

    }

    @Override
    public void execute(InsideMatchCompExpression insideMatchCompExpression, Scope scope) throws RuntimeException {
        var obj = scope.getVariable("_").getObject();
        insideMatchCompExpression.getExpression().execute(this, scope);
        var rightValue = objects.pop();
        var operator = insideMatchCompExpression.getOperator();
        if (obj instanceof IntegerObject leftValue) {
            var res = leftValue.compareTo((IntegerObject) rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        } else if (obj instanceof DoubleObject leftValue) {
            var res = leftValue.compareTo((DoubleObject) rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        } else if (obj instanceof BooleanObject leftValue) {
            var res = leftValue.compareTo((BooleanObject) rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        } else if (obj instanceof StringObject leftValue) {
            var res = leftValue.compareTo((StringObject) rightValue);
            objects.push(new BooleanObject(matchOperatorToComparison(operator, res)));
        }
    }

    @Override
    public void execute(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope) {
        var obj = scope.getVariable("_").getObject();
        var type = insideMatchTypeExpression.getType();
        boolean isInstanceOf = false;
        if (type == null) {
            isInstanceOf = obj instanceof NullObject;
        } else if (type.getTypeName().equals(INT_TYPE_NAME)) {
            isInstanceOf = obj instanceof IntegerObject;
        } else if (type.getTypeName().equals(DOUBLE_TYPE_NAME)) {
            isInstanceOf = obj instanceof DoubleObject;
        } else if (type.getTypeName().equals(STRING_TYPE_NAME)) {
            isInstanceOf = obj instanceof StringObject;
        }
        objects.push(new BooleanObject(isInstanceOf));
    }

    @Override
    public void execute(IsExpression isExpression, Scope scope) throws RuntimeException {
        isExpression.getExpression().execute(this, scope);
        var obj = objects.pop();
        var type = isExpression.getType();
        boolean isInstanceOf = false;
        if (type == null) {
            isInstanceOf = obj instanceof NullObject;
        } else if (type.getTypeName().equals(INT_TYPE_NAME)) {
            isInstanceOf = obj instanceof IntegerObject;
        } else if (type.getTypeName().equals(DOUBLE_TYPE_NAME)) {
            isInstanceOf = obj instanceof DoubleObject;
        } else if (type.getTypeName().equals(STRING_TYPE_NAME)) {
            isInstanceOf = obj instanceof StringObject;
        }
        objects.push(new BooleanObject(isInstanceOf));

    }

    @Override
    public void execute(LibFunctionCall libFunctionCall, Scope scope) throws RuntimeException {
        var name = libFunctionCall.getName();
        var arguments = libFunctionCall.getArguments();
        var count = 0;
        for(var arg : arguments) {
            arg.execute(this, scope);
            count++;
        }
        List<ExecutorObject> executorObjects = new ArrayList<>();
        while(count > 0) {
            executorObjects.add(objects.pop());
            count--;
        }

        if(name.equals("print")) {
            var text = ((StringObject)executorObjects.get(0)).getValue();
            stdLib.usePrint(text);
            objects.push(new NullObject());
        } else if (name.equals("get_input")) {
            var test = stdLib.useGetInput();
            objects.push(new StringObject(test));
        }
    }

    @Override
    public void execute(ModExpression modExpression, Scope scope) throws RuntimeException {
        modExpression.getLeftExpression().execute(this, scope);
        modExpression.getRightExpression().execute(this, scope);
        var rightValue = ((IntegerObject)objects.pop()).getValue();
        var leftValue = ((IntegerObject)objects.pop()).getValue();
        objects.push(new IntegerObject(leftValue % rightValue));
    }

    @Override
    public void execute(MulExpression mulExpression, Scope scope) throws RuntimeException {
        mulExpression.getLeftExpression().execute(this, scope);
        mulExpression.getRightExpression().execute(this, scope);
        var rightValue = objects.pop();
        var leftValue = objects.pop();
        if (leftValue instanceof IntegerObject left && rightValue instanceof IntegerObject right) {
            objects.push(new IntegerObject(left.getValue() * right.getValue()));
        } else {
            try {
                objects.push(new DoubleObject(((DoubleObject)leftValue).getValue() * ((DoubleObject)rightValue).getValue()));
            } catch (ClassCastException e) {
                throw new RuntimeException("Casting error occurred");
            }
        }
    }

    @Override
    public void execute(NullCheckExpression nullCheckExpression, Scope scope) throws RuntimeException {
        nullCheckExpression.getLeftExpression().execute(this, scope);
        nullCheckExpression.getRightExpression().execute(this, scope);
        var rightValue = objects.pop();
        var leftValue = objects.pop();
        if (leftValue instanceof NullObject) {
            objects.push(rightValue);
        } else {
            objects.push(leftValue);
        }
    }

    @Override
    public void execute(OrExpression orExpression, Scope scope) throws RuntimeException {
        orExpression.getLeftExpression().execute(this, scope);
        orExpression.getRightExpression().execute(this, scope);
        var rightValue = ((BooleanObject)objects.pop()).isValue();
        var leftValue = ((BooleanObject)objects.pop()).isValue();
        if (leftValue) {
            objects.push(new BooleanObject(true));
        } else {
            objects.push(new BooleanObject(rightValue));
        }
    }

    @Override
    public void execute(SubExpression subExpression, Scope scope) throws RuntimeException {
        subExpression.getLeftExpression().execute(this, scope);
        subExpression.getRightExpression().execute(this, scope);
        var obj = objects.peek();
        if (obj instanceof IntegerObject) {
            var rightValue = ((IntegerObject)objects.pop()).getValue();
            var leftValue = ((IntegerObject)objects.pop()).getValue();
            objects.push(new IntegerObject(leftValue - rightValue));
        } else {
            var rightValue = ((DoubleObject)objects.pop()).getValue();
            var leftValue = ((DoubleObject)objects.pop()).getValue();
            objects.push(new DoubleObject(leftValue - rightValue));
        }
    }

    @Override
    public void execute(UnaryExpression unaryExpression, Scope scope) throws RuntimeException {
        unaryExpression.getExpression().execute(this, scope);
        var obj = objects.pop();
        if (obj instanceof IntegerObject integerObject && unaryExpression.getUnaryOperator().equals("-")) {
            objects.push(new IntegerObject(-integerObject.getValue()));
        } else if (obj instanceof DoubleObject doubleObject && unaryExpression.getUnaryOperator().equals("-")) {
            objects.push(new DoubleObject(-doubleObject.getValue()));
        } else if (obj instanceof BooleanObject booleanObject && unaryExpression.getUnaryOperator().equals("!")) {
            objects.push(new BooleanObject(!booleanObject.isValue()));
        } else {
            throw new RuntimeException("Error while negating value");
        }
    }

    @Override
    public void execute(BreakInstruction breakInstruction, Scope scope) {
        breakDetected = true;
    }

    @Override
    public void execute(ContinueInstruction continueInstruction, Scope scope) {
        continueDetected = true;
    }

    @Override
    public void execute(IfInstruction ifInstruction, Scope scope) throws RuntimeException {
        ifInstruction.getCondition().execute(this, scope);
        var condition = (BooleanObject)objects.pop();
        if (condition.isValue()) {
            ifInstruction.getTrueBlock().getScope().setUpperScope(scope);
            var trueBlockScope = ifInstruction.getTrueBlock().getScope();
            ifInstruction.getTrueBlock().execute(this, trueBlockScope);
        } else {
            if (ifInstruction.getFalseBlock() == null) return;
            ifInstruction.getFalseBlock().getScope().setUpperScope(scope);
            var falseBlockScope = ifInstruction.getFalseBlock().getScope();
            ifInstruction.getFalseBlock().execute(this, falseBlockScope);
        }
    }

    @Override
    public void execute(InsideMatchInstruction insideMatchInstruction, Scope scope) throws RuntimeException {
        insideMatchInstruction.getScope().setUpperScope(scope);
        if (insideMatchInstruction.isDefault()) {
            insideMatchInstruction.getInstruction().execute(this, insideMatchInstruction.getScope());
            return;
        }
        insideMatchInstruction.getExpression().execute(this, insideMatchInstruction.getScope());
        var obj = (BooleanObject)objects.pop();
        if (obj.isValue()) {
            insideMatchInstruction.getInstruction().execute(this, insideMatchInstruction.getScope());
            matchFinished = true;
        }
    }

    @Override
    public void execute(InstructionExpression instructionExpression, Scope scope) throws RuntimeException {
        instructionExpression.getExpression().execute(this, scope);
        objects.pop();
    }

    @Override
    public void execute(MatchInstruction matchInstruction, Scope scope) throws RuntimeException {
        matchInstruction.getExpression().execute(this, scope);
        matchFinished = false;
        var obj = objects.pop();
        var matchScope = matchInstruction.getScope();
        matchScope.setUpperScope(scope);
        matchScope.getVariable("_").setObject(obj);
        for(var instruction : matchInstruction.getMatchStatements()) {
            instruction.execute(this, matchScope);
            if (returnDetected || matchFinished) {
                break;
            }
        }

    }

    @Override
    public void execute(ReturnInstruction returnInstruction, Scope scope) throws RuntimeException {
        returnDetected = true;
        callStack.peek().setReturnDetected(true);
        if (returnInstruction.getValue() != null) {
            returnInstruction.getValue().execute(this, scope);
            var obj = objects.pop();
            callStack.peek().setReturnValue(obj);
        }
    }

    @Override
    public void execute(VarDeclaration varDeclaration, Scope scope) throws RuntimeException {
        var variable = varDeclaration.getVariable();
        varDeclaration.getValue().execute(this, scope);
        var value = objects.pop();
        variable.setObject(value);
        scope.addVariable(variable);
    }

    @Override
    public void execute(WhileInstruction whileInstruction, Scope scope) throws RuntimeException {
        whileInstruction.getCondition().execute(this, scope);
        var condition = (BooleanObject)objects.pop();

        var whileScope = whileInstruction.getStatements().getScope();
        while(condition.isValue()) {
            breakDetected = continueDetected = false;
            whileInstruction.getStatements().execute(this, whileScope);
            if (breakDetected) break;
            if (continueDetected) continue;

            whileInstruction.getCondition().execute(this, scope);
            condition = (BooleanObject)objects.pop();
        }
    }

    private void pushByType(Type type, Object value) throws RuntimeException {
        ExecutorObject object;
        if (type == null) {
            objects.push(new NullObject());
            return;
        }
        object = switch (type.getTypeName()) {
            case INT_TYPE_NAME -> new IntegerObject((int) value);
            case DOUBLE_TYPE_NAME -> new DoubleObject((double) value);
            case STRING_TYPE_NAME -> new StringObject((String) value);
            case BOOL_TYPE_NAME -> new BooleanObject((boolean) value);
            default -> throw new RuntimeException("Invalid value");
        };
        objects.push(object);
    }

    private boolean matchOperatorToComparison(String operator, int comp) throws RuntimeException {
        return switch (operator) {
            case "==" -> comp == 0;
            case "!=" -> comp != 0;
            case ">" -> comp == 1;
            case "<" -> comp == -1;
            case "<=" -> comp == 0 || comp == -1;
            case ">=" -> comp == 0 || comp == 1;
            default -> throw new RuntimeException("Invalid operator");
        };
    }

    private void tryCastObject(ExecutorObject exp, Type type) throws RuntimeException {
        if (exp instanceof StringObject stringObject) {
            try {
                stringToType(stringObject, type);
            } catch (NumberFormatException | CastException e) {
                objects.push(new NullObject());
            }
        } else if (exp instanceof IntegerObject integerObject) {
            integerToType(integerObject, type);
        } else if (exp instanceof DoubleObject doubleObject) {
            doubleToType(doubleObject, type);
        } else if (exp instanceof BooleanObject booleanObject) {
            booleanToType(booleanObject, type);
        } else if (exp instanceof NullObject) {
            if (!type.isNullable()) throw new CastException("Unable to cast from null to non-nullable type");
            objects.push(new NullObject());
        } else {
            throw new RuntimeException("Unrecognized `as` expression");
        }
    }

    private void doubleToType(DoubleObject doubleObject, Type type) throws CastException {
        switch (type.getTypeName()) {
            case STRING_TYPE_NAME -> objects.push(new StringObject(String.valueOf(doubleObject.getValue())));
            case INT_TYPE_NAME -> objects.push(new IntegerObject((int)doubleObject.getValue()));
            case DOUBLE_TYPE_NAME -> objects.push(new DoubleObject(doubleObject.getValue()));
            default -> throw new CastException("Invalid cast from double");
        }
    }

    private void integerToType(IntegerObject integerObject, Type type) throws CastException {
        switch (type.getTypeName()) {
            case STRING_TYPE_NAME -> objects.push(new StringObject(String.valueOf(integerObject.getValue())));
            case DOUBLE_TYPE_NAME -> objects.push(new DoubleObject(integerObject.getValue()));
            case BOOL_TYPE_NAME -> objects.push(new BooleanObject(integerObject.getValue() != 0));
            case INT_TYPE_NAME -> objects.push(new IntegerObject(integerObject.getValue()));
            default -> throw new CastException("Invalid cast from int");
        }
    }

    private void stringToType(StringObject stringObject, Type type) throws CastException {
        switch (type.getTypeName()) {
            case INT_TYPE_NAME -> objects.push(new IntegerObject(Integer.parseInt(stringObject.getValue())));
            case DOUBLE_TYPE_NAME -> objects.push(new DoubleObject(Double.parseDouble(stringObject.getValue())));
            case BOOL_TYPE_NAME -> objects.push(new BooleanObject(Boolean.parseBoolean(stringObject.getValue())));
            case STRING_TYPE_NAME -> objects.push(new StringObject(stringObject.getValue()));
            default -> throw new CastException("Invalid cast from string");
        }
    }

    private void booleanToType(BooleanObject booleanObject, Type type) throws CastException {
        switch (type.getTypeName()) {
            case STRING_TYPE_NAME -> objects.push(new StringObject(String.valueOf(booleanObject.isValue())));
            case INT_TYPE_NAME -> objects.push(new IntegerObject(booleanObject.isValue() ? 1 : 0));
            case DOUBLE_TYPE_NAME -> objects.push(new DoubleObject(booleanObject.isValue() ? 1 : 0));
            case BOOL_TYPE_NAME -> objects.push(new BooleanObject(booleanObject.isValue()));
            default -> throw new CastException("Invalid cast from bool");
        }
    }

}
