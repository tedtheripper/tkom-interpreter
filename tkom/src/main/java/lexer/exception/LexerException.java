package lexer.exception;

public abstract class LexerException extends Exception{
    protected long line;
    protected long column;

    public LexerException(String message, long line, long column) {
        super(message);
        this.line = line;
        this.column = column;
    }
}
