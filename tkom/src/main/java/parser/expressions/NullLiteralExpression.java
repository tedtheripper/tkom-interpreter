package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class NullLiteralExpression implements Expression {

    @Override
    public void accept(Visitor visitor) {

    }
}
