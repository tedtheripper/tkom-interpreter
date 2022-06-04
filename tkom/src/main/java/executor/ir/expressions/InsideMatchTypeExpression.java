package executor.ir.expressions;

import executor.ir.Expression;
import executor.ir.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InsideMatchTypeExpression implements Expression {
    private Type type;
}
