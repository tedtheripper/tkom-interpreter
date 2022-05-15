package app;

import lexer.Tokenizer;
import lexer.exception.LexerException;
import parser.Parser;
import parser.exception.SyntaxException;
import source_loader.FileSource;
import source_loader.exception.SourceException;

import java.io.IOException;

public class StartApplication {
    public static void main(String[] args) throws IOException, SourceException, LexerException, SyntaxException {

        String filePath = "/run/media/tedtheripper/Work/semestr6/TKOM/repo/tkom-project/tkom/target/test.txt";

        if (args.length > 0)
            filePath = args[0];

        FileSource source = new FileSource(filePath);
        source.load();
        var tokenizer = new Tokenizer(source);

        var parser = new Parser(tokenizer);

        var program = parser.parse();

//        List<Token> tokens = new ArrayList<>();
//
//        try {
//            var token = tokenizer.getNextToken();
//            tokens.add(token);
//            while(token.type() != TokenType.T_ETX) {
//                token = tokenizer.getNextToken();
//                tokens.add(token);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        for(var item : tokens) {
//            System.out.println(item);
//        }

        System.out.println("FINISHED");
    }
}
