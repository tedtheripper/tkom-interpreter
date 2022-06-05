package executor.ir;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Scope {

    private Scope upperScope;
    private Map<String, Variable> declaredVariables;
    private Map<String, Function> definedFunctions;
    private List<String> variablesOrder;
    private boolean canDoReturn = false;
    private boolean canJumpLoop = false;

    public Scope(Scope upperScope) {
        this.upperScope = upperScope;
        this.declaredVariables = new HashMap<>();
        this.variablesOrder = new LinkedList<>();
        this.definedFunctions = new HashMap<>();
    }

    public Scope() {
        this.declaredVariables = new HashMap<>();
        this.variablesOrder = new LinkedList<>();
        this.definedFunctions = new HashMap<>();
    }

    public boolean addVariable(Variable variable) {
        if (declaredVariables.containsKey(variable.getName())) {
            return false;
        }
        declaredVariables.put(variable.getName(), variable);
        variablesOrder.add(variable.getName());
        return true;
    }

    public Variable getVariable(String name) {
        if (declaredVariables.containsKey(name)) {
            return declaredVariables.get(name);
        }
        if (this.upperScope != null && this.upperScope.hasVariable(name)) {
            return this.upperScope.getVariable(name);
        }
        return null;
    }

    public boolean hasVariable(String name) {
        if (declaredVariables.containsKey(name)) return true;
        return this.upperScope != null && this.upperScope.hasVariable(name);
    }

    public boolean hasFunction(String name) {
        if (definedFunctions.containsKey(name)) return true;
        return this.upperScope != null && this.upperScope.hasFunction(name);
    }

    public Function getFunction(String name) {
        if (definedFunctions.containsKey(name)) {
            return definedFunctions.get(name);
        }
        if (this.upperScope != null && this.upperScope.hasFunction(name)) {
            return this.upperScope.getFunction(name);
        }
        return null;
    }

}
