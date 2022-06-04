package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BooleanLiteralExpression implements Expression{

    private final Boolean value;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBooleanLiteralExpression(this);
    }
}
