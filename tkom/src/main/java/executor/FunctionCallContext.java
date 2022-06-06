package executor;

import executor.ir.ExecutorObject;
import executor.ir.Function;
import executor.ir.Scope;
import executor.ir.Variable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FunctionCallContext {

    private Map<String, ExecutorObject> passedArguments = new HashMap<>();
    private ExecutorObject returnValue;
    private Function functionDefinition;
    private Scope scope;
    private boolean returnDetected;

    public FunctionCallContext(List<String> parameterNames, List<ExecutorObject> values, Scope globalScope, Function functionDefinition) {
        returnDetected = false;
        int i = 0;
        for(var name : parameterNames) {
            passedArguments.put(name, values.get(i));
            i++;
        }
        scope = new Scope(copyGlobalScope(globalScope));
        for(var key : passedArguments.keySet()) {
            scope.getVariable(key).setObject(passedArguments.get(key));
        }
    }

    private Scope copyGlobalScope(Scope currentGlobalScope) {
        var globalScopeCopy = new Scope();
        globalScopeCopy.setDefinedFunctions(new HashMap<>(currentGlobalScope.getDefinedFunctions()));
        for (var name : currentGlobalScope.getDeclaredVariables().keySet()) {
            var currentVariable = currentGlobalScope.getVariable(name);
            var variable = new Variable(
                    currentVariable.getName(), currentVariable.getType(), currentVariable.isMutable(), currentVariable.getValue()
            );
            if (currentVariable.getObject() != null) {
                variable.setObject(copyObject(currentVariable.getObject()));
            }
            globalScopeCopy.addVariable(variable);
        }
        return globalScopeCopy;
    }

    private ExecutorObject copyObject(ExecutorObject o) {
        return o.copy(o);
    }

}
