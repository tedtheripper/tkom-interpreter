package executor.ir.instructions;

import executor.ir.Expression;
import executor.ir.Instruction;
import executor.ir.Variable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VarDeclaration implements Instruction {

    private Variable variable;
    private Expression value;
}
