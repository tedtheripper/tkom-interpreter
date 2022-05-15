package parser.statements;

import parser.SyntaxNode;

import java.util.List;

public class ElseBlock implements SyntaxNode {

    private final List<Statement> statements;

    public ElseBlock(List<Statement> statements) {
        this.statements = statements;
    }
}
