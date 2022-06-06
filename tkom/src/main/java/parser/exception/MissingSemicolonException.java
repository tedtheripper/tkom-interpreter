package parser.exception;

import common.Position;

public class MissingSemicolonException extends SyntaxException{
    public MissingSemicolonException(String message, Position position) {
        super(message, position);
    }
}
