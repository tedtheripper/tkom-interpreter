package lexer.exception;

public class InvalidTokenException extends LexerException{
    public InvalidTokenException(String message, long line, long column) {
        super(message, line, column);
    }
}
