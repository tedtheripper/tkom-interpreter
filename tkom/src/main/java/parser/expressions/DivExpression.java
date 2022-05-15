package parser.expressions;

public class DivExpression implements Expression {

    private final Expression leftExpression;
    private final Expression rightExpression;

    public DivExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
