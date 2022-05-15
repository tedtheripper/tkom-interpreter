package parser.statements;

import parser.expressions.Expression;

import java.util.List;

public class MatchStatement implements Statement{

    private final Expression expression;
    private final List<Statement> matchStatements;

    public MatchStatement(Expression expression, List<Statement> matchStatements) {
        this.expression = expression;
        this.matchStatements = matchStatements;
    }
}
