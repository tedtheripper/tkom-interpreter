package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class InsideMatchStatement implements Statement {

    private final Boolean isDefault;
    private final Expression expression;
    private final Statement simpleStatement;

}
