package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IfStatement implements Statement{

    private final IfBlock ifBlock;
    private final ElseBlock elseBlock;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitIfStatement(this);
    }
}
