package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class InsideMatchStatement implements Statement {

    private final Boolean isDefault;
    private final Expression expression;
    private final Statement simpleStatement;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitInsideMatchStatement(this);
    }
}
