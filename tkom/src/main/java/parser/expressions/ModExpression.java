package parser.expressions;

public class ModExpression implements Expression {
    private final Expression leftExpression;
    private final Expression rightExpression;

    public ModExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
}
