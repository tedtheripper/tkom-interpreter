package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.BuildVisitor;
import semcheck.exception.SemCheckException;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class AssignmentExpression implements Expression{

    private final Expression leftExpression;
    private final Expression rightExpression;

    @Override
    public void accept(BuildVisitor visitor) throws SemCheckException {
        visitor.visitAssignmentExpression(this);
    }
}
