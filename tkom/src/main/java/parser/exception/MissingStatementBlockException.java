package parser.exception;

import common.Position;

public class MissingStatementBlockException extends SyntaxException {

    public MissingStatementBlockException(String message, Position position) {
        super(message, position);
    }
}
