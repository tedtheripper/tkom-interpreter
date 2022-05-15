package parser.expressions;

public class AddExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;

    public AddExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
