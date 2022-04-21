package lexer;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import source_loader.InputLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class LexerTest {


    @Nested
    @DisplayName("Basic tests")
    class BasicTests {
        @Test
        void shouldCreateLexer() {
            String code =
            """
            """;
            var tokenizer = createTokenizer(code);
            assert tokenizer != null;
        }

        @Test
        void shouldReadTokens() {
            String code = """
                    int a = 0;
                    """;
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assert tokens.size() == 6;
        }
    }

    private List<Token> getTokens(Tokenizer tokenizer) {
        List<Token> tokens = new ArrayList<>();

        try {
            var token = tokenizer.getNextToken();
            tokens.add(token);
            while(token.type() != TokenType.T_ETX) {
                token = tokenizer.getNextToken();
                tokens.add(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private Tokenizer createTokenizer(String code) {
        InputLoader il = new InputLoader();
        try {
            return new Tokenizer(il.loadInput(code));
        } catch (IOException e) {
            System.out.println("Failed to create new tokenizer" + e.getMessage());
            return null;
        }
    }
}
