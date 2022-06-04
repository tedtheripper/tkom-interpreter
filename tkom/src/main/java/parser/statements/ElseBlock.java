package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.SyntaxNode;
import semcheck.Visitor;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ElseBlock implements SyntaxNode {

    private final List<Statement> statements;

    @Override
    public void accept(Visitor visitor) {
        visitor.visitElseBlock(this);
    }
}
