package parser.expressions;

public class InsideMatchCompExpression implements Expression {

    private final Expression rightExpression;
    private final Operator operator;

    public InsideMatchCompExpression(Expression rightExpression, Operator operator) {
        this.rightExpression = rightExpression;
        this.operator = operator;
    }
}
