package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DivIntExpression implements Expression {
    private final Expression leftExpression;
    private final Expression rightExpression;

}
