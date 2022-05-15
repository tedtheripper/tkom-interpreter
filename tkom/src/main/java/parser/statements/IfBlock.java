package parser.statements;

import parser.SyntaxNode;
import parser.expressions.Expression;

import java.util.List;

public class IfBlock implements SyntaxNode {

    private final Expression expression;
    private final List<Statement> statements;

    public IfBlock(Expression expression, List<Statement> statements) {
        this.expression = expression;
        this.statements = statements;
    }
}
