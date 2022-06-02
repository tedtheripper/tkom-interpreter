package parser.exception;

import common.Position;

public class MissingExpressionException extends SyntaxException{
    public MissingExpressionException(Position position) {
        super(position);
    }
}
