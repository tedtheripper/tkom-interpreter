package parser.exception;

import common.Position;

public class MissingStatementBlockException extends SyntaxException {

    public MissingStatementBlockException(Position position) {
        super(position);
    }
}
