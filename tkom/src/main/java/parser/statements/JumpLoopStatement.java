package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class JumpLoopStatement implements Statement{
    private String value;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitJumpLoopStatement(this);
    }
}
