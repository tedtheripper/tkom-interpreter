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
public class UserFunction extends Function{
    private Block instructions;

//    protected UserFunction(String name, Scope scope, Type returnType) {
//        super(name, scope, returnType);
//    }

    public void execute(Scope scope, Map<String, UserFunction> definedFunctions, List<Literal> arguments) {}

}
