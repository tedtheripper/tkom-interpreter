package executor.ir.expressions;

import executor.ir.Expression;
import executor.ir.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IsExpression implements Expression {
    private Expression expression;
    private Type type;
}
