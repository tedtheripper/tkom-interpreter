package parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.statements.Statement;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Program implements SyntaxNode{

    private final List<Statement> statements;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitProgram(this);
    }
}
