package executor.ir.expressions;

import executor.ir.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DivIntExpression implements Expression {
    private Expression leftExpression;
    private Expression rightExpression;
}
