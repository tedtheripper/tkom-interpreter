package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.Visitor;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class WhileStatement implements Statement{

    private final Expression expression;
    private final List<Statement> statements;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitWhileStatement(this);
    }
}
