package lexer.exception;

public class UnexpectedEndOfTextException extends LexerException {
    public UnexpectedEndOfTextException(String message, long line, long column) {
        super(message, line, column);
    }
}
