package parser.expressions;

import parser.Type;

public class InsideMatchTypeExpression implements Expression {

    private final Type type;

    public InsideMatchTypeExpression(Type type) {
        this.type = type;
    }
}
