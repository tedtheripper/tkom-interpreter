package executor.exceptions;

public class StackOverflowException extends RuntimeException{
    public StackOverflowException(String message) {
        super(message);
    }
}
