package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.Type;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class InsideMatchTypeExpression implements Expression {

    private final Type type;

}
