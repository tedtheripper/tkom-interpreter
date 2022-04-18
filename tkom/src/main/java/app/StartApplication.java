package app;

import lexer.Token;
import lexer.TokenType;
import lexer.Tokenizer;
import source_loader.InputLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StartApplication {
    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        InputLoader il = new InputLoader();

        Tokenizer tokenizer = new Tokenizer(il.loadInput(input));

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
