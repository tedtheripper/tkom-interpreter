package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IntegerLiteralExpression implements Expression {

    private final Integer value;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitIntegerLiteralExpression(this);
    }
}
