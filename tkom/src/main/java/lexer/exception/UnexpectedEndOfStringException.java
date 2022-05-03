package lexer.exception;

public class UnexpectedEndOfStringException extends LexerException {
    public UnexpectedEndOfStringException(String message, long line, long column) {
        super(message, line, column);
    }
}
