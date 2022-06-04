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
public class CompExpression implements Expression {
    private Expression leftExpression;
    private Expression rightExpression;
    private String operator;
}
