package parser.expressions;

public class IntegerLiteralExpression implements Expression {

    private final Integer value;

    public IntegerLiteralExpression(Integer value) {
        this.value = value;
    }
}
