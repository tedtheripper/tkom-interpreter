package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ReturnStatement implements Statement{

    private final Expression expression;
}
