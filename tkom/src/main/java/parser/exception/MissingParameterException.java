package parser.exception;

import common.Position;

public class MissingParameterException extends SyntaxException{
    public MissingParameterException(String message, Position position) {
        super(message, position);
    }
}
