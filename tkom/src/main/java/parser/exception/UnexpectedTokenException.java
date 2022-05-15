package parser.exception;

import common.Position;
import lexer.TokenType;

public class UnexpectedTokenException extends SyntaxException{

    private final TokenType receivedToken;
    private final TokenType expectedToken;

    public UnexpectedTokenException(Position position, TokenType receivedToken, TokenType expectedToken) {
        super(position);
        this.receivedToken = receivedToken;
        this.expectedToken = expectedToken;
    }
}
