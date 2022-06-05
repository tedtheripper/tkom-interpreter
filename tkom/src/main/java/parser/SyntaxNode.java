package parser;

import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

public interface SyntaxNode {

    void accept(BuildVisitor visitor) throws SemCheckException;
}
