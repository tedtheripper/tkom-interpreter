package executor.ir.expressions;

import executor.ir.Expression;
import executor.ir.Scope;
import executor.ir.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import semcheck.TypeVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Setter
@Getter
public class Identifier implements Expression {
    private String name;

    @Override
    public Type evaluateType(TypeVisitor visitor, Scope scope) throws SemCheckException {
        return visitor.visit(this, scope);
    }
}
