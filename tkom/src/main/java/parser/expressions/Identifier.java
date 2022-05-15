package parser.expressions;

public class Identifier implements Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }
}
