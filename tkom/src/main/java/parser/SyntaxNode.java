package parser;

import semcheck.Visitor;

public interface SyntaxNode {

    void accept(Visitor visitor);
}
