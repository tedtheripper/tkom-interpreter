package parser.exception;

import common.Position;

public class MissingStatementException extends SyntaxException{
    public MissingStatementException(String message, Position position) {
        super(message, position);
    }
}
