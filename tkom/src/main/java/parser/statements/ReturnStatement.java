package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ReturnStatement implements Statement{

    private final Expression expression;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitReturnStatement(this);
    }
}
