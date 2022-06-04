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

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddExpression implements Expression {

    private Expression leftExpression;
    private Expression rightExpression;

    @Override
    public Type evaluateType(TypeVisitor visitor, Scope scope) throws SemCheckException {
        return visitor.visit(this, scope);
    }
}


