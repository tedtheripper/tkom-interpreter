package parser.utils;

import lexer.TokenType;

import java.util.List;

public class OperatorUtils {

    private static List<String> compOps = List.of(
            "==", "!=", "<", "<=", ">", ">="
    );

    private static List<String> addOps = List.of(
            "+", "-"
    );

    private static List<String> mulOps = List.of(
            "*", "/", "//", "%"
    );

    private static List<String> isAsOps = List.of(
            "as", "is"
    );

    public static boolean isCompOp(TokenType type) {
        return type.getText() != null && compOps.contains(type.getText());
    }

    public static boolean isAddOp(TokenType type) {
        return type.getText() != null && addOps.contains(type.getText());
    }

    public static boolean isMulOp(TokenType type) {
        return type.getText() != null && mulOps.contains(type.getText());
    }

    public static boolean isIsAsOp(TokenType type) {
        return type.getText() != null && isAsOps.contains(type.getText());
    }
}
