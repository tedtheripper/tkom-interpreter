package lexer;


import lexer.exception.DoubleOverflowException;
import lexer.exception.IntegerOverflowException;
import lexer.exception.InvalidTokenException;
import lexer.exception.UnexpectedEndOfTextException;
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
        void testSingleCharTokens(String code, Token expectedToken) throws Exception{
            var tokenizer = createTokenizer(code);
            var token = getTokens(tokenizer).get(0);
            assertEquals(expectedToken, token);
        }

        @ParameterizedTest
        @MethodSource("mulCharOperatorsTokensFactory")
        void testMulCharTokens(String code, Token expectedToken) throws Exception{
            var tokenizer = createTokenizer(code);
            var token = getTokens(tokenizer).get(0);
            assertEquals(expectedToken, token);
        }

        @ParameterizedTest
        @MethodSource("keywordTokensFactory")
        void testKeywordsTokens(String code, Token expectedToken) throws Exception{
            var tokenizer = createTokenizer(code);
            var token = getTokens(tokenizer).get(0);
            assertEquals(expectedToken, token);
        }

        @ParameterizedTest
        @MethodSource("literalTokensFactory")
        void testLiteralsTokens(String code, Token expectedToken) throws Exception{
            var tokenizer = createTokenizer(code);
            var token = getTokens(tokenizer).get(0);
            assertEquals(expectedToken, token);
        }

        @ParameterizedTest
        @MethodSource("escapedStringsTokenFactory")
        void testEscapedStringsTokens(String code, Token expectedToken) throws Exception{
            var tokenizer = createTokenizer(code);
            var token = getTokens(tokenizer).get(0);
            assertEquals(expectedToken, token);
        }

        static Stream<Arguments> singleOperatorsTokensFactory() {
            return Stream.of(
                    Arguments.of("+", new Token(TokenType.T_ADD_OP, new Position(1,1), "+")),
                    Arguments.of("=", new Token(TokenType.T_ASSIGNMENT_OP, new Position(1,1), "=")),
                    Arguments.of("/", new Token(TokenType.T_DIV_OP, new Position(1,1), "/")),
                    Arguments.of(">", new Token(TokenType.T_GT_OP, new Position(1,1), ">")),
                    Arguments.of("<", new Token(TokenType.T_LT_OP, new Position(1,1), "<")),
                    Arguments.of("%", new Token(TokenType.T_MOD_OP, new Position(1,1), "%")),
                    Arguments.of("*", new Token(TokenType.T_MUL_OP, new Position(1,1), "*")),
                    Arguments.of("-", new Token(TokenType.T_SUB_OP, new Position(1,1), "-")),
                    Arguments.of("?", new Token(TokenType.T_TYPE_OPT, new Position(1,1), "?")),
                    Arguments.of("!", new Token(TokenType.T_UNARY_OP, new Position(1,1), "!"))
            );
        }

        static Stream<Arguments> mulCharOperatorsTokensFactory() {
            return Stream.of(
                    Arguments.of("=>", new Token(TokenType.T_ARROW, new Position(1,1), "=>")),
                    Arguments.of("//", new Token(TokenType.T_DIV_INT_OP, new Position(1,1), "//")),
                    Arguments.of("==", new Token(TokenType.T_EQUAL_OP, new Position(1,1), "==")),
                    Arguments.of(">=", new Token(TokenType.T_GE_OP, new Position(1,1), ">=")),
                    Arguments.of("<=", new Token(TokenType.T_LE_OP, new Position(1,1), "<=")),
                    Arguments.of("!=", new Token(TokenType.T_NOT_EQUAL_OP, new Position(1,1), "!=")),
                    Arguments.of("??", new Token(TokenType.T_NULL_COMP_OP, new Position(1,1), "??"))
            );
        }

        static Stream<Arguments> keywordTokensFactory() {
            return Stream.of(
                    Arguments.of("while", new Token(TokenType.T_WHILE, new Position(1,1), "while")),
                    Arguments.of("void", new Token(TokenType.T_VOID_TYPE, new Position(1,1), "void")),
                    Arguments.of("return", new Token(TokenType.T_RETURN, new Position(1,1), "return")),
                    Arguments.of("or", new Token(TokenType.T_OR_OP, new Position(1,1), "or")),
                    Arguments.of("null", new Token(TokenType.T_NULL_LITERAL, new Position(1,1), "null")),
                    Arguments.of("mutable", new Token(TokenType.T_MUTABLE, new Position(1,1), "mutable")),
                    Arguments.of("match", new Token(TokenType.T_MATCH, new Position(1,1), "match")),
                    Arguments.of("func", new Token(TokenType.T_FUNC_KEYWORD, new Position(1,1), "func")),
                    Arguments.of("else", new Token(TokenType.T_ELSE, new Position(1,1), "else")),
                    Arguments.of("default", new Token(TokenType.T_DEFAULT, new Position(1,1), "default")),
                    Arguments.of("continue", new Token(TokenType.T_CONTINUE, new Position(1,1), "continue")),
                    Arguments.of("break", new Token(TokenType.T_BREAK, new Position(1,1), "break")),
                    Arguments.of("and", new Token(TokenType.T_AND_OP, new Position(1,1), "and")),
                    Arguments.of("as", new Token(TokenType.T_AS_OP, new Position(1,1), "as")),
                    Arguments.of("is", new Token(TokenType.T_IS_OP, new Position(1,1), "is")),
                    Arguments.of("if", new Token(TokenType.T_IF, new Position(1,1), "if")),
                    Arguments.of(")", new Token(TokenType.T_PAREN_CLOSE, new Position(1,1), ")")),
                    Arguments.of("(", new Token(TokenType.T_PAREN_OPEN, new Position(1,1), "(")),
                    Arguments.of(";", new Token(TokenType.T_SEMICOLON, new Position(1,1), ";")),
                    Arguments.of("_", new Token(TokenType.T_UNDERSCORE, new Position(1,1), "_")),
                    Arguments.of(":", new Token(TokenType.T_COLON, new Position(1,1), ":")),
                    Arguments.of(",", new Token(TokenType.T_COMMA, new Position(1,1), ",")),
                    Arguments.of("}", new Token(TokenType.T_CURLY_CLOSE, new Position(1,1), "}")),
                    Arguments.of("{", new Token(TokenType.T_CURLY_OPEN, new Position(1,1), "{"))
            );
        }

        static Stream<Arguments> literalTokensFactory() {
            return Stream.of(
                    Arguments.of("\"test\"", new Token(TokenType.T_STRING_LITERAL, new Position(1, 1), "test")),
                    Arguments.of("15", new Token(TokenType.T_INT_LITERAL, new Position(1, 1), 15)),
                    Arguments.of("1.5", new Token(TokenType.T_DOUBLE_LITERAL, new Position(1, 1), 1.5)),
                    Arguments.of(".5", new Token(TokenType.T_DOUBLE_LITERAL, new Position(1, 1), 0.5)),
                    Arguments.of("1.", new Token(TokenType.T_DOUBLE_LITERAL, new Position(1, 1), 1.0)),
                    Arguments.of("true", new Token(TokenType.T_BOOL_LITERAL, new Position(1,1), true)),
                    Arguments.of("false", new Token(TokenType.T_BOOL_LITERAL, new Position(1,1), false))
            );
        }

        static Stream<Arguments> escapedStringsTokenFactory() {
            return Stream.of(
                    Arguments.of("\"test\\ntest\"", new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\ntest")),
                    Arguments.of("\"test\\btest\"", new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\btest")),
                    Arguments.of("\"test\\rtest\"", new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\rtest")),
                    Arguments.of("\"test\\ttest\"", new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\ttest")),
                    Arguments.of("\"test\\\"test\"", new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\"test")),
                    Arguments.of("\"test\\\\test\"", new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\\test")),
                    Arguments.of("\"test\\atest\"", new Token(TokenType.T_STRING_LITERAL, new Position(1,1), "test\\atest"))
            );
        }
    }

    @Nested
    @DisplayName("Identifiers tests")
    class IdentifierTokensTests {

        @ParameterizedTest
        @MethodSource("identifiersFactory")
        void testIdentifierTokens(String code, Token expectedToken) throws Exception{
            var tokenizer = createTokenizer(code);
            var token = getTokens(tokenizer).get(0);
            assertEquals(expectedToken, token);
        }

        static Stream<Arguments> identifiersFactory() {
            return Stream.of(
                    Arguments.of("a", new Token(TokenType.T_IDENTIFIER, new Position(1,1), "a")),
                    Arguments.of("_abc", new Token(TokenType.T_IDENTIFIER, new Position(1,1), "_abc")),
                    Arguments.of("ifff", new Token(TokenType.T_IDENTIFIER, new Position(1,1), "ifff")),
                    Arguments.of("whiile", new Token(TokenType.T_IDENTIFIER, new Position(1,1), "whiile"))
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
            assert tokens.size() == 6; // comment has not been tokenized
        }

    }

    @Nested
    @DisplayName("Errors tests")
    class ErrorsTests{
        @Test
        void testUnexpectedEndOfText() {
            String code = """
                    string test = "abc
                    ";
                    """;
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

    private List<Token> getTokens(Tokenizer tokenizer) throws InvalidTokenException, DoubleOverflowException, IOException, UnexpectedEndOfTextException, IntegerOverflowException {
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
