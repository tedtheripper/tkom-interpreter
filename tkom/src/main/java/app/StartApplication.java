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

import java.io.IOException;

public class StartApplication {
    public static void main(String[] args) throws IOException, SourceException, LexerException, SyntaxException, SemCheckException, RuntimeException {

        String filePath = "";

        if (args.length > 0)
            filePath = args[0];

        FileSource source = new FileSource(filePath);
        source.load();
        var tokenizer = new Tokenizer(source);

        var parser = new Parser(tokenizer);
        var program = parser.parse();

        var semCheck = new SemCheck(program);
        var interpreter = new Interpreter(semCheck.check());
        interpreter.run();

        System.out.println("DONE");
    }
}
