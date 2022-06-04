package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class JumpLoopStatement implements Statement{
    private String value;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitJumpLoopStatement(this);
    }
}
