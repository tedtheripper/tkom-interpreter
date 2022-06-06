package parser.exception;

import common.Position;

public class MissingExpressionException extends SyntaxException{
    public MissingExpressionException(String message, Position position) {
        super(message, position);
    }
}
