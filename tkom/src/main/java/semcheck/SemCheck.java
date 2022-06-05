package semcheck;

import executor.ir.GlobalBlock;
import parser.Program;
import semcheck.exception.SemCheckException;

public class SemCheck {

    private Program program;

    public SemCheck(Program program) {
        this.program = program;
    }

    public GlobalBlock check() throws SemCheckException {
        var visitor = new IRBuildVisitor();
        return visitor.export(program);
    }

}
