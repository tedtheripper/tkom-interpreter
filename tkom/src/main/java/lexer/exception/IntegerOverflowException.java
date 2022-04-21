package lexer.exception;

public class IntegerOverflowException extends LexerException{
    public IntegerOverflowException(String message, long line, long column) {
        super(message, line, column);
    }
}
