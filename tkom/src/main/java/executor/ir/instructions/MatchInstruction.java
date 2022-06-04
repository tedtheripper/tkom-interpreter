package executor.ir.instructions;

import executor.ir.Expression;
import executor.ir.Instruction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatchInstruction implements Instruction {

    private Expression expression;
    private List<Instruction> matchStatements;

}
