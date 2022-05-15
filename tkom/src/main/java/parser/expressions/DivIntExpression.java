package parser.expressions;

public class DivIntExpression implements Expression {
    private final Expression leftExpression;
    private final Expression rightExpression;

    public DivIntExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
