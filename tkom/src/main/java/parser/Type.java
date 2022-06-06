package parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Type implements SyntaxNode{

    private final boolean isNullable;
    private final String typeName;

    @Override
    public void accept(BuildVisitor visitor) {
        visitor.visitType(this);
    }
}
