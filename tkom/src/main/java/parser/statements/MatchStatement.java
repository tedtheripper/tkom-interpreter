package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MatchStatement implements Statement{

    private final Expression expression;
    private final List<Statement> matchStatements;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitMatchStatement(this);
    }
}
