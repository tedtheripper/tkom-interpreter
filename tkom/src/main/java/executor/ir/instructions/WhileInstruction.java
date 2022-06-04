package executor.ir.instructions;

import executor.ir.Block;
import executor.ir.Expression;
import executor.ir.Instruction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WhileInstruction implements Instruction {
    private Expression condition;
    private Block statements;
}
