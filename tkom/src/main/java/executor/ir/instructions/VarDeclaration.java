package executor.ir.instructions;

import executor.Executor;
import executor.exceptions.RuntimeException;
import executor.ir.Expression;
import executor.ir.Instruction;
import executor.ir.Scope;
import executor.ir.Variable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class VarDeclaration implements Instruction {

    private Variable variable;
    private Expression value;

    @Override
    public void execute(Executor executor, Scope scope) throws RuntimeException {
        executor.execute(this, scope);
    }
}
