package parser.statements;

import parser.expressions.Expression;

public class InsideMatchStatement implements Statement {

    private final Boolean isDefault;
    private final Expression expression;
    private final Statement simpleStatement;

    public InsideMatchStatement(Boolean isDefault, Expression expression, Statement simpleStatement) {
        this.isDefault = isDefault;
        this.expression = expression;
        this.simpleStatement = simpleStatement;
    }
}
