package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DoubleLiteralExpression implements Expression {

    private final Double value;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDoubleLiteralExpression(this);
    }
}
