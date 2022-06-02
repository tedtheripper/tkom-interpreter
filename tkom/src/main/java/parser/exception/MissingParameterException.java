package parser.exception;

import common.Position;

public class MissingParameterException extends SyntaxException{
    public MissingParameterException(Position position) {
        super(position);
    }
}
