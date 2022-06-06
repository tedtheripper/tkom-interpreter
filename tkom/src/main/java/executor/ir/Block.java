package executor.ir;

import executor.Executor;
import executor.exceptions.RuntimeException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Block implements IrNode{

    private Scope scope;
    private List<Instruction> instructions;

    public Block() {
        instructions = new ArrayList<>();
        scope = new Scope();
    }

    public Block(Scope scope) {
        this.scope = scope;
    }

    @Override
    public void execute(Executor executor, Scope scope) throws RuntimeException {
        executor.execute(this, scope);
    }
}
