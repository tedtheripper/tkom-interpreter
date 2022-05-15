package parser.expressions;

public class SubExpression implements Expression {
    private final Expression leftExpression;
    private final Expression rightExpression;

    public SubExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
