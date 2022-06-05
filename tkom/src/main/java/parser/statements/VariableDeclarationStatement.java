package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.Type;
import parser.expressions.Expression;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class VariableDeclarationStatement implements Statement{

    private final boolean isMutable;
    private final Type type;
    private final String name;
    private final Expression expression;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitVariableDeclarationStatement(this);
    }
}
