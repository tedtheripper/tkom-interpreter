package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class InsideMatchStatement implements Statement {

    private final boolean isDefault;
    private final Expression expression;
    private final Statement simpleStatement;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitInsideMatchStatement(this);
    }
}
