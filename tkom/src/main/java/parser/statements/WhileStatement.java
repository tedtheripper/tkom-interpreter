package parser.statements;

import parser.expressions.Expression;

import java.util.List;

public class WhileStatement implements Statement{

    private final Expression expression;
    private final List<Statement> statements;

    public WhileStatement(Expression expression, List<Statement> statements) {
        this.expression = expression;
        this.statements = statements;
    }
}
