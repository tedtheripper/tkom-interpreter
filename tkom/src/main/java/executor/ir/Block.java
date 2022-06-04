package executor.ir;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Block {

    private Scope scope;
    private List<Instruction> instructions;

    public Block() {
        instructions = new ArrayList<>();
    }

    public Block(Scope scope) {
        this.scope = scope;
    }

    public void execute(Scope scope, Map<String, Function> definedFunctions) {

    }
}
