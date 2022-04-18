package lexer;

import lexer.exception.NullTokenTypeException;
import lombok.SneakyThrows;

import java.util.Objects;

public record Token(TokenType type, Position position, Object value) {
    @SneakyThrows
    public Token(TokenType type, Position position, Object value) {
        if (Objects.isNull(type)) throw new NullTokenTypeException(String.format("Null token type detected at: line: %s, column: %s", position.column(), position.line()));
        this.type = type;
        this.position = position;
        this.value = value;
    }
}
