package parser.expressions;

import java.util.List;

public class FunctionCallExpression implements Expression {

    private final String identifier;
    private final List<Expression> argumentList;

    public FunctionCallExpression(String identifier, List<Expression> argumentList) {
        this.identifier = identifier;
        this.argumentList = argumentList;
    }
}
