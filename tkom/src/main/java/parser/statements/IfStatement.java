package parser.statements;

public class IfStatement implements Statement{

    private final IfBlock ifBlock;
    private final ElseBlock elseBlock;

    public IfStatement(IfBlock ifBlock, ElseBlock elseBlock) {
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }
}
