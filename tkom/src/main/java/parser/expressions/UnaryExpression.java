package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class UnaryExpression implements Expression {

    private final Expression expression;
    private final Operator operator;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitUnaryExpression(this);
    }
}
