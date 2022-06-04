package executor.ir.instructions;


import executor.ir.Expression;
import executor.ir.Instruction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ReturnInstruction implements Instruction {

    private Expression value;

    public ReturnInstruction() {

    }
}
