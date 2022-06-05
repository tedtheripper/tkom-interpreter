package executor.ir;

import executor.Executor;
import executor.exceptions.RuntimeException;

public interface IrNode {

    void execute(Executor executor, Scope scope) throws RuntimeException;
}
