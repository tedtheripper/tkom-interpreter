package parser;

import lexer.Tokenizer;
import lexer.exception.LexerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import parser.exception.*;
import parser.expressions.*;
import parser.statements.*;
import source_loader.TextSource;
import source_loader.exception.SourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Nested
    @DisplayName("Expression tests")
    class ExpressionTests {
        @Nested
        @DisplayName("Assignment expression tests")
        class AssignmentExpressionTests {
            @Test
            void parseAssignmentExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a = b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof AssignmentExpression);
                var expression = (AssignmentExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseAssignmentExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @Test
            void parseAssignmentExpressionFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a =;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseAssignmentExpressionMultipleFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a = b = c;
                    """;

                var parser = getParser(code);
                assertThrows(MissingSemicolonException.class, parser::parse);
            }
        }
        @Nested
        @DisplayName("Null check expression tests")
        class NullCheckExpressionTests {
            @Test
            void parseNullCheckExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a ?? b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof NullCheckExpression);
                var expression = (NullCheckExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseNullCheckExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @Test
            void parseNullCheckExpressionFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a ??;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseNullCheckMultipleExpressionTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        a ?? b ?? c;
                        """;
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof NullCheckExpression);
                var expression = (NullCheckExpression)statement;
                assertTrue(expression.getLeftExpression() instanceof NullCheckExpression);
                var leftExpression = (NullCheckExpression)expression.getLeftExpression();
                assertEquals(new Identifier("a"), leftExpression.getLeftExpression());
                assertEquals(new Identifier("b"), leftExpression.getRightExpression());
                assertEquals(new Identifier("c"), expression.getRightExpression());
            }

            @Test
            void parseAndExpressionMultipleFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a ?? b ??;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Or expression tests")
        class OrExpressionTests {
            @Test
            void parseOrExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a or b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof OrExpression);
                var expression = (OrExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseOrExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @Test
            void parseOrExpressionFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a or;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseOrMultipleExpressionTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        a or b or c;
                        """;
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof OrExpression);
                var expression = (OrExpression)statement;
                assertTrue(expression.getLeftExpression() instanceof OrExpression);
                var leftExpression = (OrExpression)expression.getLeftExpression();
                assertEquals(new Identifier("a"), leftExpression.getLeftExpression());
                assertEquals(new Identifier("b"), leftExpression.getRightExpression());
                assertEquals(new Identifier("c"), expression.getRightExpression());
            }

            @Test
            void parseAndExpressionMultipleFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a or b or;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("And expression tests")
        class AndExpressionTests {
            @Test
            void parseAndExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a and b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof AndExpression);
                var expression = (AndExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseAndExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @Test
            void parseAndExpressionFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a and;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseAndMultipleExpressionTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        a and b and c;
                        """;
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof AndExpression);
                var expression = (AndExpression)statement;
                assertTrue(expression.getLeftExpression() instanceof AndExpression);
                var leftExpression = (AndExpression)expression.getLeftExpression();
                assertEquals(new Identifier("a"), leftExpression.getLeftExpression());
                assertEquals(new Identifier("b"), leftExpression.getRightExpression());
                assertEquals(new Identifier("c"), expression.getRightExpression());
            }

            @Test
            void parseAndExpressionMultipleFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a and b and;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Comp expression tests")
        class CompExpressionTests {
            @ParameterizedTest
            @MethodSource("compOperatorFactory")
            void parseCompExpressionSuccessTest(String code, Operator expectedOperator) throws IOException, SourceException, LexerException, SyntaxException {
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof CompExpression);
                var expression = (CompExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
                assertEquals(expectedOperator, expression.getOperator());
            }

            @Test
            void parseCompExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @ParameterizedTest
            @MethodSource("compOperatorFailFactory")
            void parseCompExpressionFailTest(String code) throws IOException, SourceException, LexerException {
                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseAndMultipleExpressionTest() throws IOException, LexerException, SourceException {
                String code = """
                        a > b < c;
                        """;
                var parser = getParser(code);
                assertThrows(MissingSemicolonException.class, parser::parse);

            }

            static Stream<Arguments> compOperatorFactory() {
                return Stream.of(
                        Arguments.of("a > b;", new Operator(">")),
                        Arguments.of("a >= b;", new Operator(">=")),
                        Arguments.of("a < b;", new Operator("<")),
                        Arguments.of("a <= b;", new Operator("<=")),
                        Arguments.of("a == b;", new Operator("==")),
                        Arguments.of("a != b;", new Operator("!="))
                );
            }

            static Stream<Arguments> compOperatorFailFactory() {
                return Stream.of(
                        Arguments.of("a >;"),
                        Arguments.of("a >=;"),
                        Arguments.of("a <;"),
                        Arguments.of("a <=;"),
                        Arguments.of("a ==;"),
                        Arguments.of("a !=;")
                );
            }
        }
        @Nested
        @DisplayName("IsAs expression tests")
        class IsAsExpressionTests {

            static Stream<Arguments> isOperatorFactory() {
                return Stream.of(
                        Arguments.of("a is int;", new Type(false, "int"), new Operator("is")),
                        Arguments.of("a is int?;", new Type(true, "int"), new Operator("is")),
                        Arguments.of("a is bool;", new Type(false, "bool"), new Operator("is")),
                        Arguments.of("a is bool?;", new Type(true, "bool"), new Operator("is")),
                        Arguments.of("a is double;", new Type(false, "double"), new Operator("is")),
                        Arguments.of("a is double?;", new Type(true, "double"), new Operator("is")),
                        Arguments.of("a is string;", new Type(false, "string"), new Operator("is")),
                        Arguments.of("a is string?;", new Type(true, "string"), new Operator("is")),
                        Arguments.of("a is null;", null, new Operator("is")),
                        Arguments.of("a is void;", new Type(false, "void"), new Operator("is"))
                );
            }

            static Stream<Arguments> asOperatorFactory() {
                return Stream.of(
                        Arguments.of("a as int;", new Type(false, "int"), new Operator("as")),
                        Arguments.of("a as int?;", new Type(true, "int"), new Operator("as")),
                        Arguments.of("a as bool;", new Type(false, "bool"), new Operator("as")),
                        Arguments.of("a as bool?;", new Type(true, "bool"), new Operator("as")),
                        Arguments.of("a as double;", new Type(false, "double"), new Operator("as")),
                        Arguments.of("a as double?;", new Type(true, "double"), new Operator("as")),
                        Arguments.of("a as string;", new Type(false, "string"), new Operator("as")),
                        Arguments.of("a as string?;", new Type(true, "string"), new Operator("as")),
                        Arguments.of("a as null;", null, new Operator("as")),
                        Arguments.of("a as void;", new Type(false, "void"), new Operator("as"))
                );
            }

            static Stream<Arguments> failOperatorFactory() {
                return Stream.of(
                        Arguments.of("a is"),
                        Arguments.of("a as"),
                        Arguments.of("a is b"),
                        Arguments.of("a as b")
                );
            }

            @ParameterizedTest
            @MethodSource("isOperatorFactory")
            void parseIsExpressionSuccessTest(String code, Type expectedType, Operator expectedOperator) throws IOException, SourceException, LexerException, SyntaxException {
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof IsAsExpression);
                var expression = (IsAsExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(expectedType, expression.getType());
                assertEquals(expectedOperator, expression.getOperator());
            }

            @ParameterizedTest
            @MethodSource("asOperatorFactory")
            void parseAsExpressionSuccessTest(String code, Type expectedType, Operator expectedOperator) throws IOException, SourceException, LexerException, SyntaxException {
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof IsAsExpression);
                var expression = (IsAsExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(expectedType, expression.getType());
                assertEquals(expectedOperator, expression.getOperator());
            }


            @Test
            void parseIsAsExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @ParameterizedTest
            @MethodSource("failOperatorFactory")
            void parseIsAsExpressionFailTest(String code) throws IOException, SourceException, LexerException {
                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            @Test
            void parseIsAsExpressionMultipleIsFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a is int? is null;
                    """;

                var parser = getParser(code);
                assertThrows(MissingSemicolonException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Add expression tests")
        class AddExpressionTests {
            @Test
            void parseAddExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a + b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof AddExpression);
                var expression = (AddExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseAddExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @Test
            void parseAddExpressionFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a +;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseAddMultipleExpressionTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        a + b + c;
                        """;
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof AddExpression);
                var expression = (AddExpression)statement;
                assertTrue(expression.getLeftExpression() instanceof AddExpression);
                var leftExpression = (AddExpression)expression.getLeftExpression();
                assertEquals(new Identifier("a"), leftExpression.getLeftExpression());
                assertEquals(new Identifier("b"), leftExpression.getRightExpression());
                assertEquals(new Identifier("c"), expression.getRightExpression());
            }

            @Test
            void parseAddExpressionMultipleFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a + b +;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Additive expression tests")
        class AdditiveExpressionTests {
            @Test
            void parseAddExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a + b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof AddExpression);
                var expression = (AddExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseSubExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a - b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof SubExpression);
                var expression = (SubExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseAdditiveExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @ParameterizedTest
            @MethodSource("additiveOperatorFactory")
            void parseAdditiveExpressionFailTest(String code) throws IOException, SourceException, LexerException {
                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseAdditiveMultipleExpressionTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        a + b - c;
                        """;
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof SubExpression);
                var expression = (SubExpression)statement;
                assertTrue(expression.getLeftExpression() instanceof AddExpression);
                var leftExpression = (AddExpression)expression.getLeftExpression();
                assertEquals(new Identifier("a"), leftExpression.getLeftExpression());
                assertEquals(new Identifier("b"), leftExpression.getRightExpression());
                assertEquals(new Identifier("c"), expression.getRightExpression());
            }

            static Stream<Arguments> additiveOperatorFactory() {
                return Stream.of(
                        Arguments.of("a +;"),
                        Arguments.of("a -;"),
                        Arguments.of("a + b +;"),
                        Arguments.of("a - b -"),
                        Arguments.of("a + b -"),
                        Arguments.of("a - b +")
                );
            }
        }

        @Nested
        @DisplayName("Multiplicative expression tests")
        class MultiplicativeExpressionTests {
            @Test
            void parseMulExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a * b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MulExpression);
                var expression = (MulExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseDivExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a / b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof DivExpression);
                var expression = (DivExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseDivIntExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a // b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof DivIntExpression);
                var expression = (DivIntExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseModExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a % b;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof ModExpression);
                var expression = (ModExpression)statement;
                assertEquals(new Identifier("a"), expression.getLeftExpression());
                assertEquals(new Identifier("b"), expression.getRightExpression());
            }

            @Test
            void parseMultiplicativeExpressionWithoutRightSideSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @ParameterizedTest
            @MethodSource("multiplicativeOperatorFactory")
            void parseMultiplicativeExpressionFailTest(String code) throws IOException, SourceException, LexerException {
                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseMultiplicativeMultipleExpressionTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        a * b / c;
                        """;
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof DivExpression);
                var expression = (DivExpression)statement;
                assertTrue(expression.getLeftExpression() instanceof MulExpression);
                var leftExpression = (MulExpression)expression.getLeftExpression();
                assertEquals(new Identifier("a"), leftExpression.getLeftExpression());
                assertEquals(new Identifier("b"), leftExpression.getRightExpression());
                assertEquals(new Identifier("c"), expression.getRightExpression());
            }

            static Stream<Arguments> multiplicativeOperatorFactory() {
                return Stream.of(
                        Arguments.of("a *;"),
                        Arguments.of("a /;"),
                        Arguments.of("a //;"),
                        Arguments.of("a %;"),
                        Arguments.of("a * b /;"),
                        Arguments.of("a * b *"),
                        Arguments.of("a / b *"),
                        Arguments.of("a / b /")
                );
            }
        }

        @Nested
        @DisplayName("Unary expression tests")
        class UnaryExpressionTests {

            @Test
            void parseUnaryExpressionWithSymbolSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        !a;
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof UnaryExpression);
                var expression = (UnaryExpression)statement;
                assertEquals(new UnaryExpression(new Identifier("a"), new Operator("!")), expression);
            }

            @Test
            void parseUnaryExpressionWithoutSymbolSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @Test
            void parseUnaryExpressionFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    !;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseUnaryExpressionMultipleFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    !!a;
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Base expression tests")
        class BaseExpressionTests {

            @Test
            void parseBaseExpressionNestedExpressionSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        (a + b);
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof AddExpression);
                var expression = (AddExpression)statement;
                assertEquals(new AddExpression(new Identifier("a"), new Identifier("b")), expression);
            }

            @Test
            void parseBaseExpressionIdentifierSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a;
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Identifier);
                var expression = (Identifier)statement;
                assertEquals(new Identifier("a"), expression);
            }

            @Test
            void parseBaseExpressionFunctionCallSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                    a();
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof FunctionCallExpression);
                var expression = (FunctionCallExpression)statement;
                assertEquals(new FunctionCallExpression("a", new ArrayList<>()), expression);
            }

            @ParameterizedTest
            @MethodSource("baseLiteralFactory")
            void parseBaseExpressionLiteralSuccessTest(String code, Expression expression) throws IOException, SourceException, LexerException, SyntaxException {
                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof Expression);
                var exp = (Expression) statement;
                assertEquals(expression, exp);
            }

            @Test
            void parseBaseExpressionFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    ();
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseBaseExpressionUnexpectedTokenParenFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    (a+b;
                    """;

                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            @Test
            void parseBaseExpressionUnexpectedTokenFunctionParenFailTest() throws IOException, SourceException, LexerException {
                String code = """
                    a(;
                    """;

                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            static Stream<Arguments> baseLiteralFactory() {
                return Stream.of(
                        Arguments.of("true;", new BooleanLiteralExpression(true)),
                        Arguments.of("false;", new BooleanLiteralExpression(false)),
                        Arguments.of("12;", new IntegerLiteralExpression(12)),
                        Arguments.of("12.05;", new DoubleLiteralExpression(12.05)),
                        Arguments.of("\"test\";", new StringLiteralExpression("test")),
                        Arguments.of("null;", new NullLiteralExpression())
                );
            }
        }
    }

    @Nested
    @DisplayName("Match Statement tests")
    class MatchStatementTests {

        @Nested
        @DisplayName("Main match statement tests")
        class MainMatchStatementTests {
            @Test
            void parseMatchStatementSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            true => print("test"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                assertEquals(new CompExpression(
                        new Identifier("a"),
                        new IntegerLiteralExpression(1),
                        new Operator("==")
                ), matchSt.getExpression());
                assertFalse(matchSt.getMatchStatements().isEmpty());
            }

            @Test
            void parseMatchStatementMissingExpressionTest() throws IOException, SourceException, LexerException {
                String code = """
                        match() {
                            true => print("test"),
                        }
                        """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            static Stream<Arguments> missingBitsMatchStatementFactory() {
                return Stream.of(
                        Arguments.of("""
                                match a == 1) {
                                    true => print("test"),
                                }
                                """),
                        Arguments.of("""
                                match (a == 1 {
                                    true => print("test"),
                                }
                                """),
                        Arguments.of("""
                                match (a == 1)
                                    true => print("test"),
                                }
                                """),
                        Arguments.of("""
                                match (a == 1) {
                                    true => print("test"),
                                
                                """)
                );
            }

            @ParameterizedTest
            @MethodSource("missingBitsMatchStatementFactory")
            void parseMatchStatementMissingBitsTest(String code) throws IOException, SourceException, LexerException {
                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            @Test
            void parseMatchStatementMissingStatementsTest() throws IOException, SourceException, LexerException {
                String code = """
                        match(a == 1) {
                        
                        }
                        """;

                var parser = getParser(code);
                assertThrows(MissingStatementException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Inside match statement tests")
        class InsideMatchStatementTests {
            @Test
            void parseInsideMatchStatementSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            true => print("test"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                assertEquals(new CompExpression(
                        new Identifier("a"),
                        new IntegerLiteralExpression(1),
                        new Operator("==")
                ), matchSt.getExpression());
                var insideStatement = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement.getIsDefault());
                assertEquals(new BooleanLiteralExpression(true), insideStatement.getExpression());
                assertEquals(new FunctionCallExpression("print", List.of(new StringLiteralExpression("test"))), insideStatement.getSimpleStatement());
            }

            @Test
            void parseInsideMatchStatementMultipleSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            true => print("test"),
                            false => print("test2"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                assertEquals(new CompExpression(
                        new Identifier("a"),
                        new IntegerLiteralExpression(1),
                        new Operator("==")
                ), matchSt.getExpression());
                var insideStatement1 = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement1.getIsDefault());
                assertEquals(new BooleanLiteralExpression(true), insideStatement1.getExpression());
                assertEquals(new FunctionCallExpression("print", List.of(new StringLiteralExpression("test"))),
                        insideStatement1.getSimpleStatement());
                var insideStatement2 = (InsideMatchStatement)matchSt.getMatchStatements().get(1);
                assertEquals(false, insideStatement2.getIsDefault());
                assertEquals(new BooleanLiteralExpression(false), insideStatement2.getExpression());
                assertEquals(new FunctionCallExpression("print", List.of(new StringLiteralExpression("test2"))),
                        insideStatement2.getSimpleStatement());

            }

            @Test
            void parseInsideMatchStatementDefaultSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            default => print("test"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                assertEquals(new CompExpression(
                        new Identifier("a"),
                        new IntegerLiteralExpression(1),
                        new Operator("==")
                ), matchSt.getExpression());
                var insideStatement = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(true, insideStatement.getIsDefault());
                assertNull(insideStatement.getExpression());
                assertEquals(new FunctionCallExpression("print", List.of(new StringLiteralExpression("test"))), insideStatement.getSimpleStatement());
            }

            @Test
            void parseInsideMatchStatementComplexExpressionSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            true and even or b == 1 => print("test"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                assertEquals(new CompExpression(
                        new Identifier("a"),
                        new IntegerLiteralExpression(1),
                        new Operator("==")
                ), matchSt.getExpression());
                var insideStatement1 = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement1.getIsDefault());
                assertEquals(new OrExpression(
                        new AndExpression(new BooleanLiteralExpression(true),
                        new Identifier("even")),
                        new CompExpression(new Identifier("b"), new IntegerLiteralExpression(1), new Operator("=="))
                ), insideStatement1.getExpression());
                assertEquals(new FunctionCallExpression("print", List.of(new StringLiteralExpression("test"))),
                        insideStatement1.getSimpleStatement());

            }

            @Test
            void parseInsideMatchStatementComplexExpressionMissingExpressionTest() throws IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            true and even or => print("test"),
                        }
                        """;

                var parser = getParser(code);
               assertThrows(MissingExpressionException.class, parser::parse);
            }

            @Test
            void parseInsideMatchStatementComplexExpressionMissingArrowTest() throws IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            true and even or b == 1 return a,
                        }
                        """;

                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            @Test
            void parseInsideMatchStatementComplexExpressionMissingCommaTest() throws IOException, LexerException, SourceException {
                String code = """
                        match(a == 1) {
                            true and even or b == 1 => return a
                        }
                        """;

                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Inside match expression tests")
        class InsideMatchExpressionTests {
            @Test
            void parseInsideMatchExpressionSimpleExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                        match(a == 1) {
                            true => print("test"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                var insideStatement = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement.getIsDefault());
                assertEquals(new BooleanLiteralExpression(true), insideStatement.getExpression());
            }

            @Test
            void parseInsideMatchExpressionFunctionCallSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                        match(a == 1) {
                            divisible(_, 5) => print("test"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                var insideStatement = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement.getIsDefault());
                assertEquals(new FunctionCallExpression("divisible", List.of(new Identifier("_"), new IntegerLiteralExpression(5))),
                        insideStatement.getExpression());
            }

            @Test
            void parseInsideMatchExpressionComplexExpressionSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                        match(a == 1) {
                            (2 * b) == 5  => print("test"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                var insideStatement = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement.getIsDefault());
                assertEquals(
                        new CompExpression(
                                new MulExpression(new IntegerLiteralExpression(2), new Identifier("b")),
                                new IntegerLiteralExpression(5),
                                new Operator("==")),
                         insideStatement.getExpression());
            }

            @Test
            void parseInsideMatchExpressionIsTypeSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                        match(test(a) as int?) {
                            is null => print("null"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                var insideStatement = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement.getIsDefault());
                assertEquals(
                        new InsideMatchTypeExpression(null),
                        insideStatement.getExpression());
            }

            @Test
            void parseInsideMatchExpressionIsTypeFailTest() throws IOException, SourceException, LexerException {
                String code = """
                        match(test(a) as int?) {
                            is => print("null"),
                        }
                        """;

                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            @Test
            void parseInsideMatchExpressionCompSuccessTest() throws IOException, SourceException, LexerException, SyntaxException {
                String code = """
                        match(test(a) as int?) {
                            > 3 => print("null"),
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof MatchStatement);
                var matchSt = (MatchStatement)statement;
                var insideStatement = (InsideMatchStatement)matchSt.getMatchStatements().get(0);
                assertEquals(false, insideStatement.getIsDefault());
                assertEquals(
                        new InsideMatchCompExpression(new IntegerLiteralExpression(3), new Operator(">")),
                        insideStatement.getExpression());
            }

            @Test
            void parseInsideMatchExpressionCompFailTest() throws IOException, SourceException, LexerException {
                String code = """
                        match(test(a) as int?) {
                            > => print("null"),
                        }
                        """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }
        }
    }

    @Nested
    @DisplayName("While Statement tests")
    class WhileStatementTests {
        @Test
        void parseWhileStatementSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    while(a < 5) {
                        a = a + 1;
                    }
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof WhileStatement);
            var whileStatement = (WhileStatement)statement;
            assertEquals(new CompExpression(new Identifier("a"), new IntegerLiteralExpression(5), new Operator("<")),
                    whileStatement.getExpression());
            var statementBlock = whileStatement.getStatements();
            assertFalse(statementBlock.isEmpty());
        }

        @ParameterizedTest
        @MethodSource("missingTokensWhileFactory")
        void parseWhileStatementMissingParenOpenTest(String code) throws IOException, LexerException, SourceException {
            var parser = getParser(code);
            assertThrows(UnexpectedTokenException.class, parser::parse);
        }

        static Stream<Arguments> missingTokensWhileFactory() {
            return Stream.of(
                    Arguments.of("""
                    while a < 5) {
                        a = a + 1;
                    }
                    """),
                    Arguments.of("""
                    while (a < 5 {
                        a = a + 1;
                    }
                    """),
                    Arguments.of("""
                    while (a < 5)
                        a = a + 1;
                    }
                    """),
                    Arguments.of("""
                    while (a < 5) {
                        a = a + 1;
                    """)
            );
        }

        @Test
        void parseWhileStatementMissingExpressionTest() throws IOException, LexerException, SourceException {
            String code = """
                    while() {
                        a = a + 1;
                    }
                    """;

            var parser = getParser(code);
            assertThrows(MissingExpressionException.class, parser::parse);
        }
    }

    @Nested
    @DisplayName("If Statement tests")
    class IfStatementTests {
        @Test
        void parseIfStatementSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    if(a < 5) {
                        a = a + 1;
                    }
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof IfStatement);
            var ifStatement = (IfStatement)statement;
            assertEquals(new CompExpression(new Identifier("a"), new IntegerLiteralExpression(5), new Operator("<")),
                    ifStatement.getIfBlock().getExpression());
            assertNull(ifStatement.getElseBlock());
            var statementBlock = ifStatement.getIfBlock().getStatements();
            assertFalse(statementBlock.isEmpty());
        }

        @Test
        void parseIfStatementWithElseSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    if(a < 5) {
                        a = a + 1;
                    } else {
                        a = a + 2;
                    }
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof IfStatement);
            var ifStatement = (IfStatement)statement;
            assertEquals(new CompExpression(new Identifier("a"), new IntegerLiteralExpression(5), new Operator("<")),
                    ifStatement.getIfBlock().getExpression());
            var statementBlock = ifStatement.getIfBlock().getStatements();
            assertFalse(statementBlock.isEmpty());

            var elseStatementBlock = ifStatement.getElseBlock().getStatements();
            assertFalse(elseStatementBlock.isEmpty());
        }

        @Nested
        @DisplayName("If block tests")
        class IfBlockTests {
            @Test
            void parseIfBlockSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                    if(a < 5) {
                        a = a + 1;
                    }
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof IfStatement);
                var ifStatement = (IfStatement)statement;
                assertEquals(new CompExpression(new Identifier("a"), new IntegerLiteralExpression(5), new Operator("<")),
                        ifStatement.getIfBlock().getExpression());
                assertNull(ifStatement.getElseBlock());
                var statementBlock = ifStatement.getIfBlock().getStatements();
                assertFalse(statementBlock.isEmpty());
            }

            @Test
            void parseIfBlockMissingExpressionTest() throws IOException, LexerException, SourceException {
                String code = """
                    if() {
                        a = a + 1;
                    }
                    """;

                var parser = getParser(code);
                assertThrows(MissingExpressionException.class, parser::parse);
            }

            @ParameterizedTest
            @MethodSource("missingTokensIfBlockFactory")
            void parseIfBlockMissingParenOpenTest(String code) throws IOException, LexerException, SourceException {
                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            static Stream<Arguments> missingTokensIfBlockFactory() {
                return Stream.of(
                        Arguments.of("""
                    if a < 5) {
                        a = a + 1;
                    }
                    """),
                        Arguments.of("""
                    if (a < 5 {
                        a = a + 1;
                    }
                    """),
                        Arguments.of("""
                    if (a < 5)
                        a = a + 1;
                    }
                    """),
                        Arguments.of("""
                    if (a < 5) {
                        a = a + 1;
                    """)
                );
            }
        }

        @Nested
        @DisplayName("Else block tests")
        class ElseBlockTests {
            @Test
            void parseElseBlockSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                    if(a < 5) {
                        a = a + 1;
                    } else {
                        a = a + 2;
                    }
                    """;

                var parser = getParser(code);
                var program = parser.parse();
                var statement = program.getStatements().get(0);
                assertTrue(statement instanceof IfStatement);
                var ifStatement = (IfStatement)statement;
                assertEquals(new CompExpression(new Identifier("a"), new IntegerLiteralExpression(5), new Operator("<")),
                        ifStatement.getIfBlock().getExpression());
                assertNotNull(ifStatement.getElseBlock());
                var statementBlock = ifStatement.getIfBlock().getStatements();
                assertFalse(statementBlock.isEmpty());

                var elseStatementBlock = ifStatement.getElseBlock().getStatements();
                assertFalse(elseStatementBlock.isEmpty());
            }

            @ParameterizedTest
            @MethodSource("missingTokensElseBlockFactory")
            void parseElseBlockMissingParenOpenTest(String code) throws IOException, LexerException, SourceException {
                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }

            static Stream<Arguments> missingTokensElseBlockFactory() {
                return Stream.of(
                        Arguments.of("""
                    if(a < 5) {
                        a = a + 1;
                    } else
                        a = a + 2;
                    }
                    """),
                        Arguments.of("""
                    if(a < 5) {
                        a = a + 1;
                    } else {
                        a = a + 2;
                    """)
                );
            }
        }
    }

    @Nested
    @DisplayName("Variable declaration tests")
    class VariableDeclarationStatementTests {

        @ParameterizedTest
        @MethodSource("varDeclarationFactory")
        void parseVarDeclarationSuccessTest(String code, VariableDeclarationStatement expectedStatement) throws SyntaxException, IOException, LexerException, SourceException {

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof VariableDeclarationStatement);
            var varDecStatement = (VariableDeclarationStatement)statement;
            assertEquals(expectedStatement, varDecStatement);
        }

        static Stream<Arguments> varDeclarationFactory() {
            return Stream.of(
                    Arguments.of("""
                    mutable int? a = 1;
                    """, new VariableDeclarationStatement(
                            true, new Type(true, "int"), "a", new IntegerLiteralExpression(1)
                    )),
                    Arguments.of("""
                    int? a = 1;
                    """, new VariableDeclarationStatement(
                            false, new Type(true, "int"), "a", new IntegerLiteralExpression(1)
                    )),
                    Arguments.of("""
                    int a = 1;
                    """, new VariableDeclarationStatement(
                            false, new Type(false, "int"), "a", new IntegerLiteralExpression(1)
                    )),
                    Arguments.of("""
                    int? a = null;
                    """, new VariableDeclarationStatement(
                            false, new Type(true, "int"), "a", new NullLiteralExpression()
                    ))
            );
        }

        @ParameterizedTest
        @MethodSource("varDeclarationFailFactory")
        void parseVarDeclarationFailTest(String code) throws IOException, LexerException, SourceException {

            var parser = getParser(code);
            assertThrows(UnexpectedTokenException.class, parser::parse);

        }

        static Stream<Arguments> varDeclarationFailFactory() {
            return Stream.of(
                    Arguments.of("""
                    mutable a = 1;
                    """),
                    Arguments.of("""
                    int? = 1;
                    """)
            );
        }
    }

    @Nested
    @DisplayName("Simple statement tests")
    class SimpleStatementTests {
        @Test
        void parseJumpLoopStatementBreakSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    break;
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof JumpLoopStatement);
            var jumpStatement = (JumpLoopStatement)statement;
            assertEquals("break", jumpStatement.getValue());
        }

        @Test
        void parseJumpLoopStatementContinueSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    continue;
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof JumpLoopStatement);
            var jumpStatement = (JumpLoopStatement)statement;
            assertEquals("continue", jumpStatement.getValue());
        }

        @Test
        void parseReturnStatementWithExpressionSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    return 0;
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof ReturnStatement);
            var returnStatement = (ReturnStatement)statement;
            assertEquals(new IntegerLiteralExpression(0), returnStatement.getExpression());
        }

        @Test
        void parseReturnStatementWithoutExpressionSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    return;
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof ReturnStatement);
            var returnStatement = (ReturnStatement)statement;
            assertNull(returnStatement.getExpression());
        }
    }

    @Nested
    @DisplayName("Argument list tests")
    class ArgumentListTests {

        @Test
        void parseArgumentListSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
            String code = """
                    functionCall(a, 5);
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof FunctionCallExpression);
            var functionCall = (FunctionCallExpression)statement;
            assertEquals(2, functionCall.getArgumentList().size());
            assertEquals(List.of(new Identifier("a"), new IntegerLiteralExpression(5)), functionCall.getArgumentList());
        }

        @Test
        void parseArgumentListFailTest() throws IOException, LexerException, SourceException {
            String code = """
                    functionCall(a,);
                    """;

            var parser = getParser(code);
            assertThrows(MissingExpressionException.class, parser::parse);
        }

        @Test
        void parseArgumentListEmptySuccessTest() throws IOException, LexerException, SourceException, SyntaxException {
            String code = """
                    functionCall();
                    """;

            var parser = getParser(code);
            var program = parser.parse();
            var statement = program.getStatements().get(0);
            assertTrue(statement instanceof FunctionCallExpression);
            var functionCall = (FunctionCallExpression)statement;
            assertEquals(0, functionCall.getArgumentList().size());
        }
    }

    @Nested
    @DisplayName("Statement tests")
    class StatementTests {
        @Test
        void parseStatementWithMissingSemicolonTest() throws IOException, SourceException, LexerException {
            String code = """
                    print()
                    """;

            var parser = getParser(code);
            assertThrows(MissingSemicolonException.class, parser::parse);
        }
    }

    @Nested
    @DisplayName("Function definition tests")
    class FunctionDefinitionTests {

        @Nested
        @DisplayName("Function definition structure tests")
        class FunctionDefinitionStructureTests {

            @Test
            void parseFunctionDefSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        func test(int? a): int {
                            return a ?? 0;
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var function = program.getFunctions().values().stream().toList().get(0);
                var expectedFunctionDef = new FunctionDef(
                        "test",
                        new Type(false, "int"),
                        List.of(new Parameter(false, new Type(true, "int"), "a")),
                        List.of(new ReturnStatement(new NullCheckExpression(new Identifier("a"), new IntegerLiteralExpression(0))))
                );
                assertEquals(expectedFunctionDef, function);
            }

            @ParameterizedTest
            @MethodSource("missingTokensFunctionDefFactory")
            void parseFunctionDefMissingTokensFailTest(String code) throws IOException, SourceException, LexerException {
                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);

            }

            static Stream<Arguments> missingTokensFunctionDefFactory() {
                return Stream.of(
                        Arguments.of( """
                        func (int? a): int {
                            return a ?? 0;
                        }
                        """),
                        Arguments.of( """
                        func test int? a): int {
                            return a ?? 0;
                        }
                        """),
                        Arguments.of( """
                        func test(int? a: int {
                            return a ?? 0;
                        }
                        """),
                        Arguments.of( """
                        func test(int? a) int {
                            return a ?? 0;
                        }
                        """),
                        Arguments.of( """
                        func test(int? a): {
                            return a ?? 0;
                        }
                        """),
                        Arguments.of( """
                        func test(int? a): int
                            return a ?? 0;
                        }
                        """),
                        Arguments.of( """
                        func test(int? a): int {
                            return a ?? 0;
                        """)
                );
            }
        }

        @Nested
        @DisplayName("Function definition parameters tests")
        class FunctionDefinitionParametersTests {

            @Test
            void parseFunctionDefParametersSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        func test(int? a, string b): void {
                            int c = a ?? 0;
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var function = program.getFunctions().values().stream().toList().get(0);
                var expectedFunctionDef = new FunctionDef(
                        "test",
                        new Type(false, "void"),
                        List.of(
                                new Parameter(false, new Type(true, "int"), "a"),
                                new Parameter(false, new Type(false, "string"), "b")
                        ),
                        List.of(new VariableDeclarationStatement(false,
                                new Type(false, "int"),
                                "c",
                                new NullCheckExpression(new Identifier("a"), new IntegerLiteralExpression(0))))
                );
                assertEquals(expectedFunctionDef, function);
            }

            @Test
            void parseFunctionDefParametersMissingParameterFailTest() throws IOException, LexerException, SourceException {
                String code = """
                        func test(int? a,): int {
                            return a ?? 0;
                        }
                        """;

                var parser = getParser(code);
                assertThrows(MissingParameterException.class, parser::parse);
            }

            @Test
            void parseFunctionDefParametersMissingIdentifierFailTest() throws IOException, LexerException, SourceException {
                String code = """
                        func test(int? a, string): int {
                            return a ?? 0;
                        }
                        """;

                var parser = getParser(code);
                assertThrows(UnexpectedTokenException.class, parser::parse);
            }
        }

        @Nested
        @DisplayName("Function definition statement block tests")
        class FunctionDefinitionStatementBlockTests {

            @Test
            void parseFunctionDefStatementBlockSuccessTest() throws SyntaxException, IOException, LexerException, SourceException {
                String code = """
                        func test(int? a, string b): void {
                            int c = a ?? 0;
                        }
                        """;

                var parser = getParser(code);
                var program = parser.parse();
                var function = program.getFunctions().values().stream().toList().get(0);
                var expectedFunctionDef = new FunctionDef(
                        "test",
                        new Type(false, "void"),
                        List.of(
                                new Parameter(false, new Type(true, "int"), "a"),
                                new Parameter(false, new Type(false, "string"), "b")
                        ),
                        List.of(new VariableDeclarationStatement(false,
                                new Type(false, "int"),
                                "c",
                                new NullCheckExpression(new Identifier("a"), new IntegerLiteralExpression(0))))
                );
                assertEquals(expectedFunctionDef, function);
            }

            @Test
            void parseFunctionDefParametersMissingStatementBlockFailTest() throws IOException, LexerException, SourceException {
                String code = """
                        func test(int? a): int {
                            
                        }
                        """;

                var parser = getParser(code);
                assertThrows(MissingStatementBlockException.class, parser::parse);
            }
        }
    }

    @Nested
    @DisplayName("Program statement tests")
    class ProgramStatementTests {
        @Test
        void parseUnexpectedToken() throws IOException, SourceException, LexerException {
            String code = """
                    functionCall(a, b);
                    {}
                    """;
            var parser = getParser(code);
            assertThrows(UnexpectedTokenException.class, parser::parse);
        }
    }

    @Nested
    @DisplayName("Tests on code from initial report")
    class InitialReportSamplesParseTests {
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
            var parser = getParser(code);
            var program = parser.parse();

            var function = program.getFunctions().get("fib");
            var expectedFunctionDef = new FunctionDef(
                    "fib",
                    new Type(false, "int"),
                    List.of(new Parameter(false, new Type(false, "int"), "n")),
                    List.of(
                            new IfStatement(new IfBlock(new CompExpression(
                                    new Identifier("n"),
                                    new IntegerLiteralExpression(1),
                                    new Operator("<=")),
                                    List.of(new ReturnStatement(new Identifier("n")))), null),
                            new ReturnStatement(
                                    new AddExpression(
                                            new FunctionCallExpression("fib",
                                                    List.of(new SubExpression(new Identifier("n"), new IntegerLiteralExpression(2)))),
                                            new FunctionCallExpression("fib",
                                                    List.of(new SubExpression(new Identifier("n"), new IntegerLiteralExpression(1))))
                                    )
                            )

                    )
            );
            assertEquals(expectedFunctionDef, function);
            var statements = program.getStatements();
            var varDeclaration1 = (VariableDeclarationStatement)statements.get(0);
            var expectedVarDeclaration1 = new VariableDeclarationStatement(
                    true,
                    new Type(false, "int"),
                    "i",
                    new IntegerLiteralExpression(1)
            );
            assertEquals(expectedVarDeclaration1, varDeclaration1);

            var varDeclaration2 = (VariableDeclarationStatement)statements.get(1);
            var expectedVarDeclaration2 = new VariableDeclarationStatement(
                    false,
                    new Type(false, "int"),
                    "value",
                    new IntegerLiteralExpression(13)
            );
            assertEquals(expectedVarDeclaration2, varDeclaration2);

            var varDeclaration3 = (VariableDeclarationStatement)statements.get(2);
            var expectedVarDeclaration3 = new VariableDeclarationStatement(
                    true,
                    new Type(true, "double"),
                    "sum",
                    new NullLiteralExpression()
            );
            assertEquals(expectedVarDeclaration3, varDeclaration3);

            var whileStatement = (WhileStatement)statements.get(3);
            var expectedWhileStatement = new WhileStatement(
                    new CompExpression(new Identifier("i"), new IntegerLiteralExpression(10), new Operator("<=")),
                    List.of(
                            new AssignmentExpression(
                                    new Identifier("sum"),
                                    new AddExpression(
                                            new NullCheckExpression(new Identifier("sum"), new DoubleLiteralExpression(0.0)),
                                            new MulExpression(
                                                    new FunctionCallExpression("fib", List.of(new Identifier("i"))),
                                                    new Identifier("value")
                                            )
                                    )
                            ),
                            new AssignmentExpression(
                                    new Identifier("i"),
                                    new AddExpression(new Identifier("i"), new IntegerLiteralExpression(1))
                            )
                    )
            );
            assertEquals(expectedWhileStatement, whileStatement);

            var varDeclaration4 = (VariableDeclarationStatement)statements.get(4);
            var expectedVarDeclaration4 = new VariableDeclarationStatement(
                    false,
                    new Type(false, "string"),
                    "resMessage",
                    new AddExpression(
                            new StringLiteralExpression("Sum is: "),
                            new IsAsExpression(new Identifier("sum"), new Type(false, "string"), new Operator("as"))
                    )
            );
            assertEquals(expectedVarDeclaration4, varDeclaration4);

            var functionCall = (FunctionCallExpression)statements.get(5);
            var expectedFunctionCall = new FunctionCallExpression(
                    "print",
                    List.of(new Identifier("resMessage"))
            );

            assertEquals(expectedFunctionCall, functionCall);
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
            var parser = getParser(code);
            var program = parser.parse();

            var function1 = program.getFunctions().get("even");
            var expectedFunctionDef1 = new FunctionDef(
                    "even",
                    new Type(false, "bool"),
                    List.of(new Parameter(false, new Type(false, "int"), "value")),
                    List.of(
                            new ReturnStatement(
                                    new CompExpression(
                                            new ModExpression(new Identifier("value"), new IntegerLiteralExpression(2)),
                                            new IntegerLiteralExpression(0),
                                            new Operator("==")
                                    )
                            )

                    )
            );
            assertEquals(expectedFunctionDef1, function1);

            var function2 = program.getFunctions().get("odd_and_divisible");
            var expectedFunctionDef2 = new FunctionDef(
                    "odd_and_divisible",
                    new Type(false, "bool"),
                    List.of(
                            new Parameter(false, new Type(false, "int"), "value"),
                            new Parameter(false, new Type(false, "int"), "div")
                    ),
                    List.of(
                            new ReturnStatement(
                                    new AndExpression(
                                            new CompExpression(
                                                    new ModExpression(new Identifier("value"), new IntegerLiteralExpression(2)),
                                                    new IntegerLiteralExpression(1),
                                                    new Operator("==")
                                            ),
                                            new CompExpression(
                                                    new ModExpression(new Identifier("value"), new Identifier("div")),
                                                    new IntegerLiteralExpression(0),
                                                    new Operator("==")
                                            )
                                    )
                            )

                    )
            );
            assertEquals(expectedFunctionDef2, function2);

            var varDeclaration = (VariableDeclarationStatement)program.getStatements().get(0);
            var expectedVarDeclaration = new VariableDeclarationStatement(
                    false,
                    new Type(false, "string"),
                    "userInput",
                    new NullCheckExpression(
                            new FunctionCallExpression("get_input", List.of()),
                            new StringLiteralExpression("")
                    )
            );

            assertEquals(expectedVarDeclaration, varDeclaration);

            var matchStatement = (MatchStatement)program.getStatements().get(1);
            var expectedMatchStatement = new MatchStatement(
                    new IsAsExpression(new Identifier("userInput"), new Type(true, "int"), new Operator("as")),
                    List.of(
                            new InsideMatchStatement(
                                    false,
                                    new AndExpression(
                                            new InsideMatchTypeExpression(new Type(false, "int")),
                                            new Identifier("even")
                                    ),
                                    new FunctionCallExpression("print", List.of(
                                            new StringLiteralExpression("Number"),
                                            new Identifier("_"),
                                            new StringLiteralExpression(" is even")
                                    ))
                            ),
                            new InsideMatchStatement(
                                    false,
                                    new AndExpression(
                                            new InsideMatchTypeExpression(new Type(false, "int")),
                                            new FunctionCallExpression(
                                                    "odd_and_divisible",
                                                    List.of(
                                                            new Identifier("_"),
                                                            new IntegerLiteralExpression(3)
                                                    )
                                            )
                                    ),
                                    new FunctionCallExpression("print", List.of(
                                            new StringLiteralExpression("Number"),
                                            new Identifier("_"),
                                            new StringLiteralExpression(" is odd and divisible by 3")
                                    ))
                            ),
                            new InsideMatchStatement(
                                    true,
                                    null,
                                    new FunctionCallExpression("print", List.of(new StringLiteralExpression("Is not a number"))))
                    )
            );
            assertEquals(expectedMatchStatement, matchStatement);
        }
    }

    private Parser getParser(String code) throws IOException, SourceException, LexerException {
        var source = new TextSource(code);
        source.load();
        return new Parser(new Tokenizer(source));
    }

}
