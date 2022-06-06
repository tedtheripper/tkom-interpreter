package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DoubleLiteralExpression implements Expression {

    private final Double value;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitDoubleLiteralExpression(this);
    }
}
