package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class JumpLoopStatement implements Statement{
    private String value;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitJumpLoopStatement(this);
    }
}
