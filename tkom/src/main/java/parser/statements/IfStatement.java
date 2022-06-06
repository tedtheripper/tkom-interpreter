package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IfStatement implements Statement{

    private final IfBlock ifBlock;
    private final ElseBlock elseBlock;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitIfStatement(this);
    }
}
