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
public class Function {

    private String name;
    private Scope scope;
    private Type returnType;
    private Block instructions;

    public void execute(Scope scope, Map<String, Function> definedFunctions, List<Literal> arguments) {}

}
