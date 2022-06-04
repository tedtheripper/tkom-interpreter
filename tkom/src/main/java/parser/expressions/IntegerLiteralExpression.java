package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IntegerLiteralExpression implements Expression {

    private final Integer value;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitIntegerLiteralExpression(this);
    }
}
