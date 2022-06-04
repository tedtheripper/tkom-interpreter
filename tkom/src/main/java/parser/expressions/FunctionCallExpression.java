package parser.expressions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class FunctionCallExpression implements Expression {

    private final String identifier;
    private final List<Expression> argumentList;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitFunctionCallExpression(this);
    }
}
