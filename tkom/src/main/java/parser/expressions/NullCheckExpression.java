package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class NullCheckExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitNullCheckExpression(this);
    }
}
