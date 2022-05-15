package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class WhileStatement implements Statement{

    private final Expression expression;
    private final List<Statement> statements;
}
