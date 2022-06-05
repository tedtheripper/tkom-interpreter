package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.Type;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IsAsExpression implements Expression {

    private final Expression leftExpression;
    private final Type type;
    private final Operator operator;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitIsAsExpression(this);
    }
}
