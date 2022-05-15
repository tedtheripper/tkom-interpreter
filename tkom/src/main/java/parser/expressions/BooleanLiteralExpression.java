package parser.expressions;

public class BooleanLiteralExpression implements Expression{

    private final Boolean value;

    public BooleanLiteralExpression(Boolean value) {
        this.value = value;
    }
}
