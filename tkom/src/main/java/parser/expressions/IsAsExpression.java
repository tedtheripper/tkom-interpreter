package parser.expressions;

import parser.Type;

public class IsAsExpression implements Expression {

    private final Expression leftExpression;
    private final Type type;
    private final Operator operator;

    public IsAsExpression(Expression leftExpression, Type type, Operator operator) {
        this.leftExpression = leftExpression;
        this.type = type;
        this.operator = operator;
    }
}
