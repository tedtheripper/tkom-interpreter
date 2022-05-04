package lexer.utils;

import java.util.ArrayList;
import java.util.List;

public class LexerMappingUtils {
    private static final List<String> types = new ArrayList<>() {{
        add("bool");
        add("int");
        add("double");
        add("string");
    }};

    private static final List<String> boolLiterals = new ArrayList<>() {{
        add("true");
        add("false");
    }};

    private static final List<String> symbolicOperators = new ArrayList<>(
            List.of("+", "-", "*", "/", "//", "%", "=", "==", "!=", "<", "<=", ">", ">=", "??", "?", "!", "=>"));

    private static final List<String> symbolicKeywords = new ArrayList<>(
            List.of(":", ";", "{", "}", "(", ")", ",")
    );

    public static boolean isType(String value) {
        return types.contains(value);
    }

    public static boolean isBoolLiteral(String value) {
        return boolLiterals.contains(value);
    }

    public static boolean isSymbolicOperator(String value) {
        return symbolicOperators.contains(value);
    }

    public static boolean isSymbolicKeyword(String value) {
        return symbolicKeywords.contains(value);
    }
}
