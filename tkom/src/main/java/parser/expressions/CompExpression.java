package parser.expressions;

public class CompExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;
    private final Operator operator;

    public CompExpression(Expression leftExpression, Expression rightExpression, Operator operator) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }
}
