package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BaseExpression implements Expression {

    private final Expression expression;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBaseExpression(this);
    }
}
