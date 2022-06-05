package lexer.exception;

import lombok.Getter;

@Getter
public abstract class LexerException extends Exception{
    protected long line;
    protected long column;
    protected String message;

    protected LexerException(String message, long line, long column) {
        super(message);
        this.line = line;
        this.column = column;
    }
}
