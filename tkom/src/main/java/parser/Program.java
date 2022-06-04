package parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.statements.Statement;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Program implements SyntaxNode{

    private final List<Statement> statements;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitProgram(this);
    }
}
