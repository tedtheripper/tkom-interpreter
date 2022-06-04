package executor.ir.expressions;

import executor.ir.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddExpression implements Expression {

    private Expression leftExpression;
    private Expression rightExpression;
}


