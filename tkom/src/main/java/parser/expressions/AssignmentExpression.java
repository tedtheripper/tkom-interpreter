package parser.expressions;

public class AssignmentExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;

    public AssignmentExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
