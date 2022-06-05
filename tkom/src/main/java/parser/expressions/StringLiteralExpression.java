package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class StringLiteralExpression implements Expression{

    private final String value;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitStringLiteralExpression(this);
    }
}
