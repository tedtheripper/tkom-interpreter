package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class InsideMatchCompExpression implements Expression {

    private final Expression rightExpression;
    private final Operator operator;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitInsideMatchCompExpression(this);
    }
}
