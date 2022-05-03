package lexer;

public record Token(TokenType type, Position position, Object value) {

    public boolean is(TokenType otherType) {
        return this.type == otherType;
    }
}
