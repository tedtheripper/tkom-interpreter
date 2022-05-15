package parser.expressions;

public class StringLiteralExpression implements Expression{

    private final String value;

    public StringLiteralExpression(String value) {
        this.value = value;
    }
}
