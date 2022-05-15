package parser.expressions;

public class NullCheckExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;

    public NullCheckExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
