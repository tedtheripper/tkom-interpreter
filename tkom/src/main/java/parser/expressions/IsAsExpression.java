package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.Type;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IsAsExpression implements Expression {

    private final Expression leftExpression;
    private final Type type;
    private final Operator operator;

}
