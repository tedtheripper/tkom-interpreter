package executor.ir;

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
public class GlobalBlock {

    private Scope globalScope;
    private Map<String, Function> functions;
    private List<Instruction> instructions;



}
