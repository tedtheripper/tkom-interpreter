package lexer.exception;

public class UnknownEscapeCharacterException extends Exception{
    public UnknownEscapeCharacterException(String message) {
        super(message);
    }
}
