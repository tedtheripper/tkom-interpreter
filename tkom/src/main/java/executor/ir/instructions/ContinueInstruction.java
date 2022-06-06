package executor.ir.instructions;

import executor.Executor;
import executor.exceptions.RuntimeException;
import executor.ir.Instruction;
import executor.ir.Scope;

public class ContinueInstruction implements Instruction {
    @Override
    public void execute(Executor executor, Scope scope) throws RuntimeException {
        executor.execute(this, scope);
    }
}
