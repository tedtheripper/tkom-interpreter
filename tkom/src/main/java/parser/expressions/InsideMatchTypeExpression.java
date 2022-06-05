package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.Type;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class InsideMatchTypeExpression implements Expression {

    private final Type type;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitInsideMatchTypeExpression(this);
    }
}
