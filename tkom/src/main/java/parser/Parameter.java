package parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Parameter implements SyntaxNode{
    private final boolean isMutable;
    private final Type type;
    private final String identifier;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitParameter(this);
    }
}
