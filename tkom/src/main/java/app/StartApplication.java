package app;

import executor.Interpreter;
import executor.exceptions.RuntimeException;
import lexer.Tokenizer;
import lexer.exception.LexerException;
import parser.Parser;
import parser.exception.SyntaxException;
import semcheck.SemCheck;
import semcheck.exception.SemCheckException;
import source_loader.FileSource;
import source_loader.exception.SourceException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class StartApplication {
    public static void main(String[] args) throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {

        String filePath = "";

        if (args.length > 0)
            filePath = args[0];

        FileSource source = new FileSource(filePath);
        try {
            source.load();
            var tokenizer = new Tokenizer(source);
            var parser = new Parser(tokenizer);
            var program = parser.parse();
            var semCheck = new SemCheck(program);
            var interpreter = new Interpreter(semCheck.check());
            interpreter.runNoisy();
        } catch (FileNotFoundException ex) {
            Printer.printErrorMessage("Given file could not be found");
            return;
        } catch (SemCheckException | RuntimeException ex) {
            Printer.printErrorMessage(ex.getMessage());
            return;
        } catch (LexerException ex) {
            Printer.printLexerException(ex);
            return;
        } catch (SyntaxException ex) {
            Printer.printSyntaxException(ex);
            return;
        } catch (Exception ex) {
            Printer.printErrorMessage("Unexpected error: " + ex.getMessage());
            return;
        }

        Printer.printMessage("DONE");
    }

    static class Printer {

        private Printer() {}

        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_RESET = "\u001B[0m";

        private static final String ERROR_PREFIX = "ERROR: ";

        private static void printErrorMessage(String message) {
            System.err.println(ANSI_RED + ERROR_PREFIX + message + ANSI_RESET);
        }

        private static void printMessage(String message) {
            System.out.println(ANSI_GREEN + message + ANSI_RESET);
        }

        private static void printLexerException(LexerException exception) {
            String message = exception.getMessage() + String.format(" at (L: %d, C: %d)", exception.getLine(), exception.getLine());
            System.err.println(ANSI_RED + ERROR_PREFIX + message + ANSI_RESET);

        }

        public static void printSyntaxException(SyntaxException exception) {
            String message = exception.getMessage() + String.format(" at line: %d", exception.getPosition().line() - 1);
            System.err.println(ANSI_RED + ERROR_PREFIX + message + ANSI_RESET);
        }
    }
}