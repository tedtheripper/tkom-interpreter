package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class InsideMatchCompExpression implements Expression {

    private final Expression rightExpression;
    private final Operator operator;

    @Override
    public void accept(Visitor visitor) {

    }
}
