package executor.ir.expressions;

import executor.ir.Expression;
import executor.ir.Scope;
import executor.ir.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import semcheck.TypeVisitor;
import semcheck.exception.SemCheckException;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IsExpression implements Expression {
    private Expression expression;
    private Type type;

    @Override
    public Type evaluateType(TypeVisitor visitor, Scope scope) throws SemCheckException {
        return visitor.visit(this, scope);
    }
}
