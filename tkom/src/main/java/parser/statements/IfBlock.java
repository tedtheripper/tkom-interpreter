package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.SyntaxNode;
import parser.expressions.Expression;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class IfBlock implements SyntaxNode {

    private final Expression expression;
    private final List<Statement> statements;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitIfBlock(this);
    }
}
