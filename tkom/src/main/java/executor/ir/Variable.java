package executor.ir;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Variable {

    private String name;
    private Type type;
    private boolean isMutable;
    private Expression value;
    private ExecutorObject object;

    public Variable(String name, Type type, boolean isMutable) {
        this.name = name;
        this.type = type;
        this.isMutable = isMutable;
    }

    public Variable(String name, Type type, boolean isMutable, Expression value) {
        this.name = name;
        this.type = type;
        this.isMutable = isMutable;
        this.value = value;
    }

    public Variable(Variable variable) {
        this(variable.name, variable.type, variable.isMutable, variable.value);
    }
}
