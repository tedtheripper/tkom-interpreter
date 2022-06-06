package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BooleanLiteralExpression implements Expression{

    private final Boolean value;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitBooleanLiteralExpression(this);
    }
}
