package lexer;

public record Token(TokenType type, Position position, Object value) {
}
