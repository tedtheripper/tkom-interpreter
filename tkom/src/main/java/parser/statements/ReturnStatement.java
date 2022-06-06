package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ReturnStatement implements Statement{

    private final Expression expression;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitReturnStatement(this);
    }
}
