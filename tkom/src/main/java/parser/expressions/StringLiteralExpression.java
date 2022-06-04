package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class StringLiteralExpression implements Expression{

    private final String value;

    @Override
    public void accept(Visitor visitor) {

    }
}
