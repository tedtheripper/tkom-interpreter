package executor.ir.expressions;

import executor.Executor;
import executor.exceptions.RuntimeException;
import executor.ir.Expression;
import executor.ir.Scope;
import executor.ir.Type;
import lombok.*;
import semcheck.TypeVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class AssignmentExpression implements Expression {

    private String variableName;
    private Expression rightSide;

    @Override
    public Type evaluateType(TypeVisitor visitor, Scope scope) throws SemCheckException {
        return visitor.visit(this, scope);
    }

    @Override
    public void execute(Executor executor, Scope scope) throws RuntimeException {
        executor.execute(this, scope);
    }
}
