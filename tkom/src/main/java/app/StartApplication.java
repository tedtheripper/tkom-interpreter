package app;

import lexer.Token;
import lexer.TokenType;
import lexer.Tokenizer;
import source_loader.InputLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartApplication {
    public static void main(String[] args) throws IOException {

        String filePath = "";

        if (args.length > 0)
            filePath = args[0];

        InputLoader il = new InputLoader(filePath);

        var tokenizer = new Tokenizer(il.loadFile());
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
