package lexer.exception;

public class DoubleOverflowException extends LexerException{
    public DoubleOverflowException(String message, long line, long column) {
        super(message, line, column);
    }
}
