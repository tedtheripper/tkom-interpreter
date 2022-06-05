package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BooleanLiteralExpression implements Expression{

    private final Boolean value;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitBooleanLiteralExpression(this);
    }
}
