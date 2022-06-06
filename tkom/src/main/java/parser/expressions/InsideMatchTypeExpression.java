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
public class InsideMatchTypeExpression implements Expression {

    private final Type type;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitInsideMatchTypeExpression(this);
    }
}
