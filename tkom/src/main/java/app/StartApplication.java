package app;

import lexer.Token;
import lexer.TokenType;
import lexer.Tokenizer;
import source_loader.FileSource;
import source_loader.exception.SourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartApplication {
    public static void main(String[] args) throws IOException, SourceException {

        String filePath = "";

        if (args.length > 0)
            filePath = args[0];

        FileSource source = new FileSource(filePath);
        source.load();
        var tokenizer = new Tokenizer(source);
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

        for(var item : tokens) {
            System.out.println(item);
        }
    }
}
