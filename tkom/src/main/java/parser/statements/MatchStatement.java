package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MatchStatement implements Statement{

    private final Expression expression;
    private final List<Statement> matchStatements;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitMatchStatement(this);
    }
}
