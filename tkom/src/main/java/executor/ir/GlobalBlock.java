package executor.ir;

import executor.Executor;
import executor.exceptions.RuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GlobalBlock implements IrNode{

    private Scope globalScope;
    private Map<String, Function> functions;
    private List<Instruction> instructions;

    @Override
    public void execute(Executor executor, Scope scope) throws RuntimeException {
        executor.execute(this, scope);
    }

}
