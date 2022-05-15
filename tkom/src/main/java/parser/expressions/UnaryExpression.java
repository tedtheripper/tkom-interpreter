package parser.expressions;

public class UnaryExpression implements Expression {

    private final Expression expression;
    private final Operator operator;

    public UnaryExpression(Expression expression, Operator operator) {
        this.expression = expression;
        this.operator = operator;
    }
}
