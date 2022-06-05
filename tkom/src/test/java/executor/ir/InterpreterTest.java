package executor.ir;

import executor.Interpreter;
import executor.exceptions.CastException;
import executor.exceptions.RuntimeException;
import executor.exceptions.StackOverflowException;
import lexer.Tokenizer;
import lexer.exception.LexerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.exception.SyntaxException;
import semcheck.SemCheck;
import semcheck.exception.SemCheckException;
import source_loader.TextSource;
import source_loader.exception.SourceException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InterpreterTest {

    @Nested
    @DisplayName("Examples from report: 1")
    class ReportExample1 {
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
                    sum = (sum ?? 0.0) + (fib(i) * value as double);
                    i = i + 1;
                }
                                
                string resMessage = "Sum is: " + (sum as string);
                print(resMessage);
                              
                """;

        @Test
        void shouldProperlyInterpretCode() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            var irTree = new SemCheck(program).check();
            var interpreter = new Interpreter(irTree);
            interpreter.runNoisy();
        }
    }

    @Nested
    @DisplayName("Examples from report: 2")
    class ReportExample2 {
        String code = """
                func even(int value): bool {
                    return value % 2 == 0;
                }
                                
                func odd_and_divisible(int value, int div): bool {
                    return value % 2 == 1 and value % div == 0;
                }
                                
                string userInput = get_input();
                
                match(userInput as int?) {     # return value of this expression can be accessed via '_'
                    is int and even(_ as int) => print("Number" + (_ as string) + " is even"),
                    is int and odd_and_divisible(_ as int, 3) => print("Number" +  (_ as string) + " is odd and divisible by 3"),
                    default => print("Is not a number"),
                }
                              
                """;

        @Test
        void shouldProperlyInterpretCodeWithNumericInputFromUser() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            var irTree = new SemCheck(program).check();
            var interpreter = new Interpreter(irTree);
            System.setIn(new ByteArrayInputStream("123".getBytes(StandardCharsets.UTF_8)));
            PrintStream out = new PrintStream(System.out);
            System.setOut(out);
            interpreter.runNoisy();
        }

        @Test
        void shouldProperlyInterpretCodeWithSymbolicInputFromUser() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            var irTree = new SemCheck(program).check();
            var interpreter = new Interpreter(irTree);
            System.setIn(new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8)));
            PrintStream out = new PrintStream(System.out);
            System.setOut(out);
            interpreter.runNoisy();
        }

        @Test
        void shouldProperlyInterpretCodeWithNumericEventInputFromUser() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            var irTree = new SemCheck(program).check();
            var interpreter = new Interpreter(irTree);
            System.setIn(new ByteArrayInputStream("120".getBytes(StandardCharsets.UTF_8)));
            PrintStream out = new PrintStream(System.out);
            System.setOut(out);
            interpreter.runNoisy();
        }
    }

    @Nested
    @DisplayName("Stackoverflow exception tests")
    class StackOverflowTests {
        String code = """
                func fib(int n) : int {
                    if (n <= 1) {
                        return n;
                    }
                    return fib(n - 2) + fib(n - 1);
                }
                                
                int res = fib(144);
                print(res as string);
                              
                """;

        @Test
        void shouldThrowStackOverflowExceptionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            var irTree = new SemCheck(program).check();
            var interpreter = new Interpreter(irTree);
            assertThrows(StackOverflowException.class, interpreter::runNoisy);
        }
    }

    @Nested
    @DisplayName("Cast exception tests")
    class CastExceptionTests {
        String code = """                 
                int? res = null;
                print(res as string);
                              
                """;

        @Test
        void shouldThrowCastExceptionTest() throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {
            var source = new TextSource(code);
            source.load();
            var lexer = new Tokenizer(source);
            var parser = new Parser(lexer);
            var program = parser.parse();
            var irTree = new SemCheck(program).check();
            var interpreter = new Interpreter(irTree);
            assertThrows(CastException.class, interpreter::runNoisy);
        }
    }


}
