package parser.expressions;

public class MainExpression implements Expression {
    private final Expression leftExpression;
    private final Expression rightExpression;

    public MainExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
