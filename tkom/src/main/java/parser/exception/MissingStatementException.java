package parser.exception;

import common.Position;

public class MissingStatementException extends SyntaxException{
    public MissingStatementException(Position position) {
        super(position);
    }
}
