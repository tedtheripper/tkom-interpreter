package parser.exception;

import common.Position;
import lombok.Getter;

@Getter
public abstract class SyntaxException extends Exception {

    protected final Position position;
    protected String message;

    protected SyntaxException(String message, Position position) {
        super(message);
        this.position = position;
        this.message = message;
    }
}
