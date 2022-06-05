package executor.ir.instructions;

import executor.ir.Expression;
import executor.ir.Instruction;
import executor.ir.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InsideMatchInstruction implements Instruction {

    private Scope scope;
    private boolean isDefault;
    private Expression expression;
    private Instruction instruction;

}
