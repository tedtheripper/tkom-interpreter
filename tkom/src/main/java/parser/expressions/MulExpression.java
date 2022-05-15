package parser.expressions;

public class MulExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;

    public MulExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
