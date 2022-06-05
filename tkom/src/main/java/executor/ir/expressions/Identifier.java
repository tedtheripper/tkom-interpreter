package executor.ir.expressions;

import executor.Executor;
import executor.exceptions.RuntimeException;
import executor.ir.Expression;
import executor.ir.Scope;
import executor.ir.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import semcheck.TypeVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class Identifier implements Expression {
    private String name;

    @Override
    public Type evaluateType(TypeVisitor visitor, Scope scope) throws SemCheckException {
        return visitor.visit(this, scope);
    }

    @Override
    public void execute(Executor executor, Scope scope) throws RuntimeException {
        executor.execute(this, scope);
    }
}
