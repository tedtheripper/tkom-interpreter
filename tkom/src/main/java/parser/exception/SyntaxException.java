package parser.exception;

import common.Position;

public abstract class SyntaxException extends Exception {

    protected final Position position;

    protected SyntaxException(Position position) {
        this.position = position;
    }
}
