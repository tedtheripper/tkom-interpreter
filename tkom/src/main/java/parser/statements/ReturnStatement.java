package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.expressions.Expression;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ReturnStatement implements Statement{

    private final Expression expression;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitReturnStatement(this);
    }
}
