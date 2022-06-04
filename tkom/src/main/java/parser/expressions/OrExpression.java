package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class OrExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;

    @Override
    public void accept(Visitor visitor) {

    }
}
