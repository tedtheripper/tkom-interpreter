package executor.ir.expressions;

import executor.ir.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnaryExpression implements Expression {
    private String unaryOperator;
    private Expression expression;
}
