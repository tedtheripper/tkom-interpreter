package parser.exception;

import common.Position;

public class MissingSemicolonException extends SyntaxException{
    public MissingSemicolonException(Position position) {
        super(position);
    }
}
