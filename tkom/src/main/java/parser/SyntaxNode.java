package parser;

import semcheck.Visitor;
import semcheck.exception.SemCheckException;

public interface SyntaxNode {

    void accept(Visitor visitor) throws SemCheckException;
}
