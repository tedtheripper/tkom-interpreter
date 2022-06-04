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
public class InstructionExpression implements Instruction {

    private Scope scope;
    private Expression expression;
}
