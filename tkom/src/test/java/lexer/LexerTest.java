package lexer;


import lexer.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import source_loader.InputLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        void shouldReadTokens() throws Exception{
            String code = """
                    int a = 0;
                    """;
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assert tokens.size() == 6;
        }
    }

    @Nested
    @DisplayName("Single token tests")
    class SingleTokenTests {

        @ParameterizedTest
        @MethodSource("singleOperatorsTokensFactory")
        void testSingleCharTokens(String code, List<Token> expectedTokens) throws Exception{
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assertEquals(expectedTokens, tokens);
        }

        @ParameterizedTest
        @MethodSource("mulCharOperatorsTokensFactory")
        void testMulCharTokens(String code, List<Token> expectedTokens) throws Exception{
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assertEquals(expectedTokens, tokens);
        }

        @ParameterizedTest
        @MethodSource("keywordTokensFactory")
        void testKeywordsTokens(String code, List<Token> expectedTokens) throws Exception{
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assertEquals(expectedTokens, tokens);
        }

        @ParameterizedTest
        @MethodSource("literalTokensFactory")
        void testLiteralsTokens(String code, List<Token> expectedTokens) throws Exception{
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assertEquals(expectedTokens, tokens);
        }

        @ParameterizedTest
        @MethodSource("escapedStringsTokenFactory")
        void testEscapedStringsTokens(String code, List<Token> expectedTokens) throws Exception{
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assertEquals(expectedTokens, tokens);
        }

        static Stream<Arguments> singleOperatorsTokensFactory() {
            return Stream.of(
                    Arguments.of("+", List.of(new Token(TokenType.T_ADD_OP, new Position(1,1), "+"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("=", List.of(new Token(TokenType.T_ASSIGNMENT_OP, new Position(1,1), "="), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("/", List.of(new Token(TokenType.T_DIV_OP, new Position(1,1), "/"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of(">", List.of(new Token(TokenType.T_GT_OP, new Position(1,1), ">"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("<", List.of(new Token(TokenType.T_LT_OP, new Position(1,1), "<"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("%", List.of(new Token(TokenType.T_MOD_OP, new Position(1,1), "%"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("*", List.of(new Token(TokenType.T_MUL_OP, new Position(1,1), "*"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("-", List.of(new Token(TokenType.T_SUB_OP, new Position(1,1), "-"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("?", List.of(new Token(TokenType.T_TYPE_OPT, new Position(1,1), "?"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("!", List.of(new Token(TokenType.T_UNARY_OP, new Position(1,1), "!"), new Token(TokenType.T_ETX, new Position(1, 2), null)))
            );
        }

        static Stream<Arguments> mulCharOperatorsTokensFactory() {
            return Stream.of(
                    Arguments.of("=>", List.of(new Token(TokenType.T_ARROW, new Position(1,1), "=>"), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("//", List.of(new Token(TokenType.T_DIV_INT_OP, new Position(1,1), "//"), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("==", List.of(new Token(TokenType.T_EQUAL_OP, new Position(1,1), "=="), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of(">=", List.of(new Token(TokenType.T_GE_OP, new Position(1,1), ">="), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("<=", List.of(new Token(TokenType.T_LE_OP, new Position(1,1), "<="), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("!=", List.of(new Token(TokenType.T_NOT_EQUAL_OP, new Position(1,1), "!="), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("??", List.of(new Token(TokenType.T_NULL_COMP_OP, new Position(1,1), "??"), new Token(TokenType.T_ETX, new Position(1, 3), null)))
            );
        }

        static Stream<Arguments> keywordTokensFactory() {
            return Stream.of(
                    Arguments.of("while", List.of(new Token(TokenType.T_WHILE, new Position(1,1), "while"), new Token(TokenType.T_ETX, new Position(1, 6), null))),
                    Arguments.of("void", List.of(new Token(TokenType.T_VOID_TYPE, new Position(1,1), "void"), new Token(TokenType.T_ETX, new Position(1, 5), null))),
                    Arguments.of("return", List.of(new Token(TokenType.T_RETURN, new Position(1,1), "return"), new Token(TokenType.T_ETX, new Position(1, 7), null))),
                    Arguments.of("or", List.of(new Token(TokenType.T_OR_OP, new Position(1,1), "or"), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("null", List.of(new Token(TokenType.T_NULL_LITERAL, new Position(1,1), "null"), new Token(TokenType.T_ETX, new Position(1, 5), null))),
                    Arguments.of("mutable", List.of(new Token(TokenType.T_MUTABLE, new Position(1,1), "mutable"), new Token(TokenType.T_ETX, new Position(1, 8), null))),
                    Arguments.of("match", List.of(new Token(TokenType.T_MATCH, new Position(1,1), "match"), new Token(TokenType.T_ETX, new Position(1, 6), null))),
                    Arguments.of("func", List.of(new Token(TokenType.T_FUNC_KEYWORD, new Position(1,1), "func"), new Token(TokenType.T_ETX, new Position(1, 5), null))),
                    Arguments.of("else", List.of(new Token(TokenType.T_ELSE, new Position(1,1), "else"), new Token(TokenType.T_ETX, new Position(1, 5), null))),
                    Arguments.of("default", List.of(new Token(TokenType.T_DEFAULT, new Position(1,1), "default"), new Token(TokenType.T_ETX, new Position(1, 8), null))),
                    Arguments.of("continue", List.of(new Token(TokenType.T_CONTINUE, new Position(1,1), "continue"), new Token(TokenType.T_ETX, new Position(1, 9), null))),
                    Arguments.of("break", List.of(new Token(TokenType.T_BREAK, new Position(1,1), "break"), new Token(TokenType.T_ETX, new Position(1, 6), null))),
                    Arguments.of("and", List.of(new Token(TokenType.T_AND_OP, new Position(1,1), "and"), new Token(TokenType.T_ETX, new Position(1, 4), null))),
                    Arguments.of("as", List.of(new Token(TokenType.T_AS_OP, new Position(1,1), "as"), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("is", List.of(new Token(TokenType.T_IS_OP, new Position(1,1), "is"), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("if", List.of(new Token(TokenType.T_IF, new Position(1,1), "if"), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of(")", List.of(new Token(TokenType.T_PAREN_CLOSE, new Position(1,1), ")"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("(", List.of(new Token(TokenType.T_PAREN_OPEN, new Position(1,1), "("), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of(";", List.of(new Token(TokenType.T_SEMICOLON, new Position(1,1), ";"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("_", List.of(new Token(TokenType.T_UNDERSCORE, new Position(1,1), "_"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of(":", List.of(new Token(TokenType.T_COLON, new Position(1,1), ":"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of(",", List.of(new Token(TokenType.T_COMMA, new Position(1,1), ","), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("}", List.of(new Token(TokenType.T_CURLY_CLOSE, new Position(1,1), "}"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("{", List.of(new Token(TokenType.T_CURLY_OPEN, new Position(1,1), "{"), new Token(TokenType.T_ETX, new Position(1, 2), null)))
            );
        }

        static Stream<Arguments> literalTokensFactory() {
            return Stream.of(
                    Arguments.of("\"test\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1, 1), "test"), new Token(TokenType.T_ETX, new Position(1, 7), null))),
                    Arguments.of("15", List.of(new Token(TokenType.T_INT_LITERAL, new Position(1, 1), 15), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("1.5", List.of(new Token(TokenType.T_DOUBLE_LITERAL, new Position(1, 1), 1.5), new Token(TokenType.T_ETX, new Position(1, 4), null))),
                    Arguments.of(".5", List.of(new Token(TokenType.T_DOUBLE_LITERAL, new Position(1, 1), 0.5), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("1.", List.of(new Token(TokenType.T_DOUBLE_LITERAL, new Position(1, 1), 1.0), new Token(TokenType.T_ETX, new Position(1, 3), null))),
                    Arguments.of("true", List.of(new Token(TokenType.T_BOOL_LITERAL, new Position(1,1), true), new Token(TokenType.T_ETX, new Position(1, 5), null))),
                    Arguments.of("false", List.of(new Token(TokenType.T_BOOL_LITERAL, new Position(1,1), false), new Token(TokenType.T_ETX, new Position(1, 6), null)))
            );
        }

        static Stream<Arguments> escapedStringsTokenFactory() {
            return Stream.of(
                    Arguments.of("\"test\\ntest\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\ntest"), new Token(TokenType.T_ETX, new Position(1, 13), null))),
                    Arguments.of("\"test\\btest\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\btest"), new Token(TokenType.T_ETX, new Position(1, 13), null))),
                    Arguments.of("\"test\\rtest\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\rtest"), new Token(TokenType.T_ETX, new Position(1, 13), null))),
                    Arguments.of("\"test\\ttest\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\ttest"), new Token(TokenType.T_ETX, new Position(1, 13), null))),
                    Arguments.of("\"test\\\"test\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\"test"), new Token(TokenType.T_ETX, new Position(1, 13), null))),
                    Arguments.of("\"test\\\\test\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\\test"), new Token(TokenType.T_ETX, new Position(1, 13), null))),
                    Arguments.of("\"test\\atest\"", List.of(new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\\atest"), new Token(TokenType.T_ETX, new Position(1, 13), null)))
            );
        }
    }

    @Nested
    @DisplayName("Identifiers tests")
    class IdentifierTokensTests {

        @ParameterizedTest
        @MethodSource("identifiersFactory")
        void testIdentifierTokens(String code, List<Token> expectedTokens) throws Exception{
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assertEquals(expectedTokens, tokens);
        }

        static Stream<Arguments> identifiersFactory() {
            return Stream.of(
                    Arguments.of("a", List.of(new Token(TokenType.T_IDENTIFIER, new Position(1,1), "a"), new Token(TokenType.T_ETX, new Position(1, 2), null))),
                    Arguments.of("_abc", List.of(new Token(TokenType.T_IDENTIFIER, new Position(1,1), "_abc"), new Token(TokenType.T_ETX, new Position(1, 5), null))),
                    Arguments.of("ifff", List.of(new Token(TokenType.T_IDENTIFIER, new Position(1,1), "ifff"), new Token(TokenType.T_ETX, new Position(1, 5), null))),
                    Arguments.of("whiile", List.of(new Token(TokenType.T_IDENTIFIER, new Position(1,1), "whiile"), new Token(TokenType.T_ETX, new Position(1, 7), null)))
            );
        }

        @Test
        void testIdentifierTokens() throws Exception{
            String code = """
                    # commented stuff
                    int a = 1;
                    """;
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            var expectedTokens = List.of(
                    new Token(TokenType.T_TYPE, new Position(2, 1), "int"),
                    new Token(TokenType.T_IDENTIFIER, new Position(2, 5), "a"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(2, 7), "="),
                    new Token(TokenType.T_INT_LITERAL, new Position(2, 9), 1),
                    new Token(TokenType.T_SEMICOLON, new Position(2, 10), ";"),
                    new Token(TokenType.T_ETX, new Position(3, 1), null)
            );

            assert tokens.size() == 6; // comment has not been tokenized
            assertEquals(expectedTokens, tokens);
        }

    }

    @Nested
    @DisplayName("Errors tests")
    class ErrorsTests{
        @Test
        void testUnexpectedEndOfString() {
            String code = """
                    string test = "abc
                    ";
                    """;
            var tokenizer = createTokenizer(code);
            assertThrows(UnexpectedEndOfStringException.class, () -> getTokens(tokenizer));
        }

        @Test
        void testUnexpectedEndOfText() {
            String code = """
                    string test = "abc""";
            var tokenizer = createTokenizer(code);
            assertThrows(UnexpectedEndOfTextException.class, () -> getTokens(tokenizer));
        }

        @Test
        void testIntegerOverflow() {
            String code = """
                    int value = 99999999999999999999999999;
                    """;
            var tokenizer = createTokenizer(code);
            assertThrows(IntegerOverflowException.class, () -> getTokens(tokenizer));
        }

        @Test
        void testDoubleOverflow() {
            String code = """
                    double value = 0.000000000000000000000000000000000001;
                    """;
            var tokenizer = createTokenizer(code);
            assertThrows(DoubleOverflowException.class, () -> getTokens(tokenizer));
        }

        @Test
        void testInvalidToken() {
            String code = """
                    $test = 0;
                    """;
            var tokenizer = createTokenizer(code);
            assertThrows(InvalidTokenException.class, () -> getTokens(tokenizer));
        }
    }

    @Nested
    @DisplayName("Tests on code from initial report")
    class InitialReportSamplesTests {
        @Test
        void shouldCorrectlyReadAllTokensSample1() throws Exception{
            String code = """
                    # function calculates Nth fibonacci number
                    func fib(int n) : int {
                        if (n <= 1) {
                            return n;
                        }
                        return fib(n - 2) + fib(n - 1);
                    }
                                        
                    mutable int i = 1;
                    int value = 13;
                    mutable double? sum = null;
                                        
                    while (i <= 10) {
                        sum = (sum ?? 0.0) + fib(i) * value;
                        i = i + 1;
                    }
                                        
                    string resMessage = "Sum is: " + (sum as string);
                    print(resMessage);
                    """;
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assert tokens.size() == 101;
        }

        @Test
        void shouldCorrectlyReadAllTokensSample2() throws Exception {
            String code = """
                    func even(int value): bool {
                        return value % 2 == 0;
                    }
                                        
                    func odd_and_divisible(int value, int div): bool {
                        return value % 2 == 1 and value % div == 0;
                    }
                                        
                    string userInput = get_input() ?? "";
                    
                    match(userInput as int?) {     # return value of this expression can be accessed via '_'
                        is int and even => print("Number", _, " is even"),
                        is int and odd_and_divisible(_, 3) => print("Number", _,  " is odd and divisible by 3"),
                        default => print("Is not a number"),
                    }
                    """;
            var tokenizer = createTokenizer(code);
            var tokens = getTokens(tokenizer);
            assert tokens.size() == 102;
        }
    }

    @Test
    void badDoubleLiteralTests() {
        String code = "1.2.3";
        var tokenizer = createTokenizer(code);
        assertThrows(InvalidTokenException.class, () -> getTokens(tokenizer));

    }

    @Test
    void badIntegerLiteralTests() {
        String code = "1O3";
        var tokenizer = createTokenizer(code);
        assertThrows(InvalidTokenException.class, () -> getTokens(tokenizer));
    }

    @Test
    void commentWithETXTests() throws InvalidTokenException, DoubleOverflowException, IOException, UnexpectedEndOfTextException, IntegerOverflowException, UnexpectedEndOfStringException {
        String code = "#asd";
        var tokenizer = createTokenizer(code);
        var tokens = getTokens(tokenizer);
        assert tokens.size() == 1;
        var expectedToken = new Token(TokenType.T_ETX, new Position(1, 5), null);

        assertEquals(expectedToken, tokens.get(0));
    }

    private List<Token> getTokens(Tokenizer tokenizer) throws InvalidTokenException, DoubleOverflowException, IOException, UnexpectedEndOfTextException, IntegerOverflowException, UnexpectedEndOfStringException {
        List<Token> tokens = new ArrayList<>();

        if (tokenizer == null) {
            return tokens;
        }

        var token = tokenizer.getNextToken();
        tokens.add(token);
        while(token.type() != TokenType.T_ETX) {
            token = tokenizer.getNextToken();
            tokens.add(token);
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
