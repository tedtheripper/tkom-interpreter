package parser.expressions;

public class BaseExpression implements Expression {

    private final Expression expression;

    public BaseExpression(Expression expression) {
        this.expression = expression;
    }
}
