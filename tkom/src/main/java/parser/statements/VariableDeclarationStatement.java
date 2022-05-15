package parser.statements;

import parser.Type;
import parser.expressions.Expression;

public class VariableDeclarationStatement implements Statement{

    private final boolean isMutable;
    private final Type type;
    private final String name;
    private final Expression expression;

    public VariableDeclarationStatement(boolean isMutable, Type type, String name, Expression expression) {
        this.isMutable = isMutable;
        this.type = type;
        this.name = name;
        this.expression = expression;
    }
}
