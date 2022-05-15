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
        return compOps.contains(type.getText());
    }

    public static boolean isAddOp(TokenType type) {
        return addOps.contains(type.getText());
    }

    public static boolean isMulOp(TokenType type) {
        return mulOps.contains(type.getText());
    }

    public static boolean isIsAsOp(TokenType type) {
        return isAsOps.contains(type.getText());
    }
}
