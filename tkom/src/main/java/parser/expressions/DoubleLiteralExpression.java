package parser.expressions;

public class DoubleLiteralExpression implements Expression {

    private final Double value;

    public DoubleLiteralExpression(Double value) {
        this.value = value;
    }
}
