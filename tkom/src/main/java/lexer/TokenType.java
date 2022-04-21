package lexer;

public enum TokenType {
    T_ADD_OP("+"),
    T_AND_OP("and"),
    T_ARROW("=>"),
    T_ASSIGNMENT_OP("="),
    T_AS_OP("as"),
    T_BOOL_LITERAL,
    T_BREAK("break"),
    T_COLON(":"),
    T_COMMA(","),
    T_CONTINUE("continue"),
    T_CURLY_CLOSE("}"),
    T_CURLY_OPEN("{"),
    T_DEFAULT("default"),
    T_DIV_INT_OP("//"),
    T_DIV_OP("/"),
    T_DOUBLE_LITERAL,
    T_ELSE("else"),
    T_EQUAL_OP("=="),
    T_ETX,
    T_FUNC_KEYWORD("func"),
    T_GE_OP(">="),
    T_GT_OP(">"),
    T_IDENTIFIER,
    T_IF("if"),
    T_INT_LITERAL,
    T_INVALID,
    T_IS_OP("is"),
    T_LE_OP("<="),
    T_LT_OP("<"),
    T_MATCH("match"),
    T_MOD_OP("%"),
    T_MUL_OP("*"),
    T_MUTABLE("mutable"),
    T_NOT_EQUAL_OP("!="),
    T_NULL_COMP_OP("??"),
    T_NULL_LITERAL("null"),
    T_OR_OP("or"),
    T_PAREN_CLOSE(")"),
    T_PAREN_OPEN("("),
    T_RETURN("return"),
    T_SEMICOLON(";"),
    T_STRING_LITERAL,
    T_SUB_OP("-"),
    T_TYPE,
    T_TYPE_OPT("?"),
    T_UNARY_OP("!"),
    T_UNDERSCORE("_"),
    T_VOID_TYPE("void"),
    T_WHILE("while");

    @lombok.Getter
    private final String text;

    TokenType(String text) {
        this.text = text;
    }

    TokenType() {
        this.text = null;
    }

    public static TokenType fromString(String text) {
        for(TokenType t : TokenType.values()) {
            if (t.text != null && t.text.equals(text)) {
                return t;
            }
        }
        throw new IllegalArgumentException("No TokenType with text " + text + "found");
    }
}