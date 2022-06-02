package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IfStatement implements Statement{

    private final IfBlock ifBlock;
    private final ElseBlock elseBlock;

}
