package executor.ir;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LibFunction extends Function{
    public LibFunction(String name, Scope scope, Type returnType) {
        super(name, scope, returnType);
    }
}
