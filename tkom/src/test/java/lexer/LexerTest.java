package lexer;


import common.Position;
import lexer.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import source_loader.exception.SourceException;
import source_loader.TextSource;

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
            var expectedTokens = List.of(
                    new Token(TokenType.T_FUNC_KEYWORD, new Position(2, 1), "func"),
                    new Token(TokenType.T_IDENTIFIER, new Position(2, 6), "fib"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(2, 9), "("),
                    new Token(TokenType.T_TYPE, new Position(2, 10), "int"),
                    new Token(TokenType.T_IDENTIFIER, new Position(2, 14), "n"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(2, 15), ")"),
                    new Token(TokenType.T_COLON, new Position(2, 17), ":"),
                    new Token(TokenType.T_TYPE, new Position(2, 19), "int"),
                    new Token(TokenType.T_CURLY_OPEN, new Position(2, 23), "{"),
                    new Token(TokenType.T_IF, new Position(3, 5), "if"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(3, 8), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(3, 9), "n"),
                    new Token(TokenType.T_LE_OP, new Position(3, 11), "<="),
                    new Token(TokenType.T_INT_LITERAL, new Position(3, 14), 1),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(3, 15), ")"),
                    new Token(TokenType.T_CURLY_OPEN, new Position(3, 17), "{"),
                    new Token(TokenType.T_RETURN, new Position(4, 9), "return"),
                    new Token(TokenType.T_IDENTIFIER, new Position(4, 16), "n"),
                    new Token(TokenType.T_SEMICOLON, new Position(4, 17), ";"),
                    new Token(TokenType.T_CURLY_CLOSE, new Position(5, 5), "}"),
                    new Token(TokenType.T_RETURN, new Position(6, 5), "return"),
                    new Token(TokenType.T_IDENTIFIER, new Position(6, 12), "fib"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(6, 15), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(6, 16), "n"),
                    new Token(TokenType.T_SUB_OP, new Position(6, 18), "-"),
                    new Token(TokenType.T_INT_LITERAL, new Position(6, 20), 2),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(6, 21), ")"),
                    new Token(TokenType.T_ADD_OP, new Position(6, 23), "+"),
                    new Token(TokenType.T_IDENTIFIER, new Position(6, 25), "fib"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(6, 28), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(6, 29), "n"),
                    new Token(TokenType.T_SUB_OP, new Position(6, 31), "-"),
                    new Token(TokenType.T_INT_LITERAL, new Position(6, 33), 1),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(6, 34), ")"),
                    new Token(TokenType.T_SEMICOLON, new Position(6, 35), ";"),
                    new Token(TokenType.T_CURLY_CLOSE, new Position(7, 1), "}"),
                    new Token(TokenType.T_MUTABLE, new Position(9, 1), "mutable"),
                    new Token(TokenType.T_TYPE, new Position(9, 9), "int"),
                    new Token(TokenType.T_IDENTIFIER, new Position(9, 13), "i"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(9, 15), "="),
                    new Token(TokenType.T_INT_LITERAL, new Position(9, 17), 1),
                    new Token(TokenType.T_SEMICOLON, new Position(9, 18), ";"),
                    new Token(TokenType.T_TYPE, new Position(10, 1), "int"),
                    new Token(TokenType.T_IDENTIFIER, new Position(10, 5), "value"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(10, 11), "="),
                    new Token(TokenType.T_INT_LITERAL, new Position(10, 13), 13),
                    new Token(TokenType.T_SEMICOLON, new Position(10, 15), ";"),
                    new Token(TokenType.T_MUTABLE, new Position(11, 1), "mutable"),
                    new Token(TokenType.T_TYPE, new Position(11, 9), "double"),
                    new Token(TokenType.T_TYPE_OPT, new Position(11, 15), "?"),
                    new Token(TokenType.T_IDENTIFIER, new Position(11, 17), "sum"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(11, 21), "="),
                    new Token(TokenType.T_NULL_LITERAL, new Position(11, 23), "null"),
                    new Token(TokenType.T_SEMICOLON, new Position(11, 27), ";"),
                    new Token(TokenType.T_WHILE, new Position(13, 1), "while"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(13, 7), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(13, 8), "i"),
                    new Token(TokenType.T_LE_OP, new Position(13, 10), "<="),
                    new Token(TokenType.T_INT_LITERAL, new Position(13, 13), 10),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(13, 15), ")"),
                    new Token(TokenType.T_CURLY_OPEN, new Position(13, 17), "{"),
                    new Token(TokenType.T_IDENTIFIER, new Position(14, 5), "sum"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(14, 9), "="),
                    new Token(TokenType.T_PAREN_OPEN, new Position(14, 11), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(14, 12), "sum"),
                    new Token(TokenType.T_NULL_COMP_OP, new Position(14, 16), "??"),
                    new Token(TokenType.T_INT_LITERAL, new Position(14, 19), 0),
                    new Token(TokenType.T_DOUBLE_LITERAL, new Position(14, 20), 0.0),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(14, 22), ")"),
                    new Token(TokenType.T_ADD_OP, new Position(14, 24), "+"),
                    new Token(TokenType.T_IDENTIFIER, new Position(14, 26), "fib"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(14, 29), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(14, 30), "i"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(14, 31), ")"),
                    new Token(TokenType.T_MUL_OP, new Position(14, 33), "*"),
                    new Token(TokenType.T_IDENTIFIER, new Position(14, 35), "value"),
                    new Token(TokenType.T_SEMICOLON, new Position(14, 40), ";"),
                    new Token(TokenType.T_IDENTIFIER, new Position(15, 5), "i"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(15, 7), "="),
                    new Token(TokenType.T_IDENTIFIER, new Position(15, 9), "i"),
                    new Token(TokenType.T_ADD_OP, new Position(15, 11), "+"),
                    new Token(TokenType.T_INT_LITERAL, new Position(15, 13), 1),
                    new Token(TokenType.T_SEMICOLON, new Position(15, 14), ";"),
                    new Token(TokenType.T_CURLY_CLOSE, new Position(16, 1), "}"),
                    new Token(TokenType.T_TYPE, new Position(18, 1), "string"),
                    new Token(TokenType.T_IDENTIFIER, new Position(18, 8), "resMessage"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(18, 19), "="),
                    new Token(TokenType.T_STRING_LITERAL, new Position(18, 21), "Sum is: "),
                    new Token(TokenType.T_ADD_OP, new Position(18, 32), "+"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(18, 34), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(18, 35), "sum"),
                    new Token(TokenType.T_AS_OP, new Position(18, 39), "as"),
                    new Token(TokenType.T_TYPE, new Position(18, 42), "string"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(18, 48), ")"),
                    new Token(TokenType.T_SEMICOLON, new Position(18, 49), ";"),
                    new Token(TokenType.T_IDENTIFIER, new Position(19, 1), "print"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(19, 6), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(19, 7), "resMessage"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(19, 17), ")"),
                    new Token(TokenType.T_SEMICOLON, new Position(19, 18), ";"),
                    new Token(TokenType.T_ETX, new Position(20, 1), null)
            );

            assert tokens.size() == 101;
            assertEquals(expectedTokens, tokens);
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
            var expectedTokens = List.of(
                    new Token(TokenType.T_FUNC_KEYWORD, new Position(1, 1), "func"),
                    new Token(TokenType.T_IDENTIFIER, new Position(1, 6), "even"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(1, 10), "("),
                    new Token(TokenType.T_TYPE, new Position(1, 11), "int"),
                    new Token(TokenType.T_IDENTIFIER, new Position(1, 15), "value"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(1, 20), ")"),
                    new Token(TokenType.T_COLON, new Position(1, 21), ":"),
                    new Token(TokenType.T_TYPE, new Position(1, 23), "bool"),
                    new Token(TokenType.T_CURLY_OPEN, new Position(1, 28), "{"),
                    new Token(TokenType.T_RETURN, new Position(2, 5), "return"),
                    new Token(TokenType.T_IDENTIFIER, new Position(2, 12), "value"),
                    new Token(TokenType.T_MOD_OP, new Position(2, 18), "%"),
                    new Token(TokenType.T_INT_LITERAL, new Position(2, 20), 2),
                    new Token(TokenType.T_EQUAL_OP, new Position(2, 22), "=="),
                    new Token(TokenType.T_INT_LITERAL, new Position(2, 25), 0),
                    new Token(TokenType.T_SEMICOLON, new Position(2, 26), ";"),
                    new Token(TokenType.T_CURLY_CLOSE, new Position(3, 1), "}"),
                    new Token(TokenType.T_FUNC_KEYWORD, new Position(5, 1), "func"),
                    new Token(TokenType.T_IDENTIFIER, new Position(5, 6), "odd_and_divisible"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(5, 23), "("),
                    new Token(TokenType.T_TYPE, new Position(5, 24), "int"),
                    new Token(TokenType.T_IDENTIFIER, new Position(5, 28), "value"),
                    new Token(TokenType.T_COMMA, new Position(5, 33), ","),
                    new Token(TokenType.T_TYPE, new Position(5, 35), "int"),
                    new Token(TokenType.T_IDENTIFIER, new Position(5, 39), "div"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(5, 42), ")"),
                    new Token(TokenType.T_COLON, new Position(5, 43), ":"),
                    new Token(TokenType.T_TYPE, new Position(5, 45), "bool"),
                    new Token(TokenType.T_CURLY_OPEN, new Position(5, 50), "{"),
                    new Token(TokenType.T_RETURN, new Position(6, 5), "return"),
                    new Token(TokenType.T_IDENTIFIER, new Position(6, 12), "value"),
                    new Token(TokenType.T_MOD_OP, new Position(6, 18), "%"),
                    new Token(TokenType.T_INT_LITERAL, new Position(6, 20), 2),
                    new Token(TokenType.T_EQUAL_OP, new Position(6, 22), "=="),
                    new Token(TokenType.T_INT_LITERAL, new Position(6, 25), 1),
                    new Token(TokenType.T_AND_OP, new Position(6, 27), "and"),
                    new Token(TokenType.T_IDENTIFIER, new Position(6, 31), "value"),
                    new Token(TokenType.T_MOD_OP, new Position(6, 37), "%"),
                    new Token(TokenType.T_IDENTIFIER, new Position(6, 39), "div"),
                    new Token(TokenType.T_EQUAL_OP, new Position(6, 43), "=="),
                    new Token(TokenType.T_INT_LITERAL, new Position(6, 46), 0),
                    new Token(TokenType.T_SEMICOLON, new Position(6, 47), ";"),
                    new Token(TokenType.T_CURLY_CLOSE, new Position(7, 1), "}"),
                    new Token(TokenType.T_TYPE, new Position(9, 1), "string"),
                    new Token(TokenType.T_IDENTIFIER, new Position(9, 8), "userInput"),
                    new Token(TokenType.T_ASSIGNMENT_OP, new Position(9, 18), "="),
                    new Token(TokenType.T_IDENTIFIER, new Position(9, 20), "get_input"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(9, 29), "("),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(9, 30), ")"),
                    new Token(TokenType.T_NULL_COMP_OP, new Position(9, 32), "??"),
                    new Token(TokenType.T_STRING_LITERAL, new Position(9, 35), ""),
                    new Token(TokenType.T_SEMICOLON, new Position(9, 37), ";"),
                    new Token(TokenType.T_MATCH, new Position(11, 1), "match"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(11, 6), "("),
                    new Token(TokenType.T_IDENTIFIER, new Position(11, 7), "userInput"),
                    new Token(TokenType.T_AS_OP, new Position(11, 17), "as"),
                    new Token(TokenType.T_TYPE, new Position(11, 20), "int"),
                    new Token(TokenType.T_TYPE_OPT, new Position(11, 23), "?"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(11, 24), ")"),
                    new Token(TokenType.T_CURLY_OPEN, new Position(11, 26), "{"),
                    new Token(TokenType.T_IS_OP, new Position(12, 5), "is"),
                    new Token(TokenType.T_TYPE, new Position(12, 8), "int"),
                    new Token(TokenType.T_AND_OP, new Position(12, 12), "and"),
                    new Token(TokenType.T_IDENTIFIER, new Position(12, 16), "even"),
                    new Token(TokenType.T_ARROW, new Position(12, 21), "=>"),
                    new Token(TokenType.T_IDENTIFIER, new Position(12, 24), "print"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(12, 29), "("),
                    new Token(TokenType.T_STRING_LITERAL, new Position(12, 30), "Number"),
                    new Token(TokenType.T_COMMA, new Position(12, 38), ","),
                    new Token(TokenType.T_UNDERSCORE, new Position(12, 40), "_"),
                    new Token(TokenType.T_COMMA, new Position(12, 41), ","),
                    new Token(TokenType.T_STRING_LITERAL, new Position(12, 43), " is even"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(12, 53), ")"),
                    new Token(TokenType.T_COMMA, new Position(12, 54), ","),
                    new Token(TokenType.T_IS_OP, new Position(13, 5), "is"),
                    new Token(TokenType.T_TYPE, new Position(13, 8), "int"),
                    new Token(TokenType.T_AND_OP, new Position(13, 12), "and"),
                    new Token(TokenType.T_IDENTIFIER, new Position(13, 16), "odd_and_divisible"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(13, 33), "("),
                    new Token(TokenType.T_UNDERSCORE, new Position(13, 34), "_"),
                    new Token(TokenType.T_COMMA, new Position(13, 35), ","),
                    new Token(TokenType.T_INT_LITERAL, new Position(13, 37), 3),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(13, 38), ")"),
                    new Token(TokenType.T_ARROW, new Position(13, 40), "=>"),
                    new Token(TokenType.T_IDENTIFIER, new Position(13, 43), "print"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(13, 48), "("),
                    new Token(TokenType.T_STRING_LITERAL, new Position(13, 49), "Number"),
                    new Token(TokenType.T_COMMA, new Position(13, 57), ","),
                    new Token(TokenType.T_UNDERSCORE, new Position(13, 59), "_"),
                    new Token(TokenType.T_COMMA, new Position(13, 60), ","),
                    new Token(TokenType.T_STRING_LITERAL, new Position(13, 63), " is odd and divisible by 3"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(13, 91), ")"),
                    new Token(TokenType.T_COMMA, new Position(13, 92), ","),
                    new Token(TokenType.T_DEFAULT, new Position(14, 5), "default"),
                    new Token(TokenType.T_ARROW, new Position(14, 13), "=>"),
                    new Token(TokenType.T_IDENTIFIER, new Position(14, 16), "print"),
                    new Token(TokenType.T_PAREN_OPEN, new Position(14, 21), "("),
                    new Token(TokenType.T_STRING_LITERAL, new Position(14, 22), "Is not a number"),
                    new Token(TokenType.T_PAREN_CLOSE, new Position(14, 39), ")"),
                    new Token(TokenType.T_COMMA, new Position(14, 40), ","),
                    new Token(TokenType.T_CURLY_CLOSE, new Position(15, 1), "}"),
                    new Token(TokenType.T_ETX, new Position(16, 1), null)
            );

            assert tokens.size() == 102;
            assertEquals(expectedTokens, tokens);
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
    void commentWithETXTests() throws InvalidTokenException, DoubleOverflowException, IOException, UnexpectedEndOfTextException, IntegerOverflowException, UnexpectedEndOfStringException, SourceException {
        String code = "#asd";
        var tokenizer = createTokenizer(code);
        var tokens = getTokens(tokenizer);
        assert tokens.size() == 1;
        var expectedToken = new Token(TokenType.T_ETX, new Position(1, 5), null);

        assertEquals(expectedToken, tokens.get(0));
    }

    @Test
    void escapedStringWithETX() {
        String code = "\"test\\";
        var tokenizer = createTokenizer(code);
        assertThrows(UnexpectedEndOfTextException.class, () -> getTokens(tokenizer));
    }


    private List<Token> getTokens(Tokenizer tokenizer) throws InvalidTokenException, DoubleOverflowException, IOException, UnexpectedEndOfTextException, IntegerOverflowException, UnexpectedEndOfStringException, SourceException {
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
        TextSource ts = new TextSource(code);
        ts.load();
        try {
            return new Tokenizer(ts);
        } catch (Exception e) {
            System.out.println("Failed to create new tokenizer" + e.getMessage());
            return null;
        }
    }
}
