package semcheck;

import lexer.Tokenizer;
import lexer.exception.LexerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.exception.SyntaxException;
import semcheck.exception.SemCheckException;
import source_loader.TextSource;
import source_loader.exception.SourceException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SemCheckTest {
    @Nested
    @DisplayName("Small tests")
    class SmallTests {
        @Test
        void functionRedefinitionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                func test(int a):int {
                    return 2;
                }
                
                func test(double a):int{
                    return 2.0;
                }
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Nested
        @DisplayName("Missing return tests")
        class MissingReturnTests {
            @Test
            void returnInElseBlockMissingTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                func test(int a):int {
                    if (a > 0) {
                        return a;
                    } else {
                        print(a as string);
                    }
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

            @Test
            void returnInIfBlockMissingTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                func test(int a):int {
                    if (a > 0) {
                        print(a as string);
                    } else {
                        return a;
                    }
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

            @Test
            void returnInBlockBlockMissingTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                func test(int a):int {
                    print("TEST");
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

            @Test
            void returnInMatchWithoutDefaultMissingTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                func test(int a):int {
                    match(a) {
                        > 0 => return a,
                        <= 0 => print("TEST"),
                    }
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

            @Test
            void returnInMatchWithDefaultMissingTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                func test(int a):int {
                    match(a) {
                        > 0 => return a,
                        <= 5 => print("TEST"),
                        default => return 0,
                    }
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

            @Test
            void returnInMatchInsideIfBlockMissingTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                func test(int a):int {
                    if (a > -5) {
                        match(a) {
                            > 0 => return a,
                            <= 5 => print("TEST"),
                            default => return 0,
                        }
                    } else {
                        return 0;
                    }
                    
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

        }

        @Test
        void returnInElseBlockTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                func test(int a):int {
                    if (a > 0) {
                        return a;
                    } else {
                        return -a;
                    }
                }
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            new SemCheck(program).check();
        }

        @Nested
        @DisplayName("Invalid expression tests")
        class InvalidExpressionsTests {
            @Test
            void invalidExpressionInIfConditionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                int a = 4;
                if(a + 2) {
                    print("TEST");
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

            @Test
            void invalidExpressionInWhileConditionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                int a = 4;
                while(a + 2) {
                    print("TEST");
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }

            @Test
            void invalidExpressionInMatchStatementConditionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
                String code = """                 
                mutable int a = 5;
                match(2 + 2) {
                    a = 2 => print("TEST"),
                }
                              
                """;
                var source = new TextSource(code);
                source.load();
                var lexer = new Tokenizer(source);
                var parser = new Parser(lexer);
                var program = parser.parse();
                assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
            }
        }

        @Test
        void returnOutsideOfFunctionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5;
                return a;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Test
        void breakOutsideOfLoopTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5;
                break;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Test
        void continueOutsideOfLoopTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5;
                continue;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Test
        void variableRedeclarationTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5;
                string a = "TEST";
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Test
        void constVariableReassignmentTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5;
                a = 6;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Test
        void nonIdentifierExpressionAssignmentTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5;
                2+2 = 6;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Test
        void missingVariableTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                a = 6;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            assertThrows(SemCheckException.class, () -> new SemCheck(program).check());
        }

        @Test
        void baseExpressionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = (5*5);
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            new SemCheck(program).check();
        }

        @Test
        void booleanLiteralTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                bool a = false;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            new SemCheck(program).check();
        }

        @Test
        void divTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                double a = 5 / 2;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            new SemCheck(program).check();
        }

        @Test
        void divIntTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5 // 2;
                              
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            new SemCheck(program).check();
        }

        @Test
        void insideMatchCompExpressionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5 // 2;
                match(a as string) {
                    == "2" or == "5" => print("TEST"),
                }
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            new SemCheck(program).check();
        }

        @Test
        void unaryExpressionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException {
            String code = """                 
                int a = 5 // 2;
                int b = -a;
                """;
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            new SemCheck(program).check();
        }

    }
}
