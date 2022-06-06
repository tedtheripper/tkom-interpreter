package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class CompExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;
    private final Operator operator;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitCompExpression(this);
    }
}
