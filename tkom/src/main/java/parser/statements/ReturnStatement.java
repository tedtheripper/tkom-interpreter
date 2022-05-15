package parser.statements;

import parser.expressions.Expression;

public class ReturnStatement implements Statement{

    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }
}
