package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BaseExpression implements Expression {

    private final Expression expression;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitBaseExpression(this);
    }
}
