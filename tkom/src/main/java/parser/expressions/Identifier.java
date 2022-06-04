package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Identifier implements Expression {

    private final String name;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitIdentifier(this);
    }
}
