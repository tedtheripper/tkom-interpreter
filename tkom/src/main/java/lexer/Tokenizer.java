package lexer;

import lexer.exception.DoubleOverflowException;
import lexer.exception.IntegerOverflowException;
import lexer.exception.InvalidTokenException;
import lexer.exception.UnexpectedEndOfTextException;

import java.io.IOException;
import java.io.PushbackReader;

public class Tokenizer {

    private Token currentToken;
    private int currentCharacter;

    private long currentLine;
    private long currentColumn;

    private final PushbackReader fileSourceReader;

    public Tokenizer(PushbackReader fileSourceReader) {
        this.fileSourceReader = fileSourceReader;
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    public void getNextCharacter() throws IOException {
        if (this.fileSourceReader != null) {
            currentCharacter = this.fileSourceReader.read();
        }
    }

    public Token getNextToken() throws IOException, DoubleOverflowException, IntegerOverflowException, UnexpectedEndOfTextException, InvalidTokenException {

        while (Character.isWhitespace(currentCharacter)) {
            getNextCharacter();
        }

        if (currentCharacter == '#') {
            getNextCharacter();
            while(currentCharacter != '\n') {
                getNextCharacter();
            }
            getNextCharacter();
        }

        if (currentCharacter == -1) return new Token(TokenType.T_ETX, new Position(currentLine, currentColumn), null);

        if (Character.isAlphabetic(currentCharacter) || currentCharacter == '_') {
            if (tryBuildIdentifierOrKeyword()) {
                return currentToken;
            } else currentToken = new Token(TokenType.T_INVALID, new Position(currentLine, currentColumn), null);
        } else if (Character.isDigit(currentCharacter) || currentCharacter == '.') {
            if (tryBuildNumber()) {
                return currentToken;
            } else currentToken = new Token(TokenType.T_INVALID, new Position(currentLine, currentColumn), null);
        } else if (currentCharacter == '"') {
            if (tryBuildString()) {
                return currentToken;
            } else currentToken = new Token(TokenType.T_INVALID, new Position(currentLine, currentColumn), null);
        } else {
            if (tryBuildSymbol()) {
                return currentToken;
            } else currentToken = new Token(TokenType.T_INVALID, new Position(currentLine, currentColumn), null);
        }

        if (currentToken.type() == TokenType.T_INVALID) throw new InvalidTokenException(
                String.format("Invalid token found at L:%d, C:%d", currentToken.position().line(), currentToken.position().column()));
        return null;
    }

    private boolean tryBuildNumber() throws IOException, IntegerOverflowException, DoubleOverflowException {
        if (!Character.isDigit(currentCharacter) && currentCharacter != '.') return false;

        int value = 0;
        if (Character.isDigit(currentCharacter) && currentCharacter != '0') {
            try {
                value = Math.addExact(value, (currentCharacter - '0'));
                getNextCharacter();
                while (Character.isDigit(currentCharacter)) {
                    value = value * 10 + (currentCharacter - '0');
                    getNextCharacter();
                }
            } catch (ArithmeticException e) {
                throw new IntegerOverflowException(String.format("Integer overflow found L:%d, C:%d", currentLine, currentColumn));
            }
        }
        if (currentCharacter == '.') {
            long fractionPart = 0;
            int decimalPlaces = 0;
            getNextCharacter();
            while (Character.isDigit(currentCharacter)) {
                try {
                    fractionPart = Math.addExact(fractionPart * 10, (currentCharacter - '0'));
                } catch (ArithmeticException e) {
                    throw new DoubleOverflowException(String.format("Double overflow found L:%d, C:%d", currentLine, currentColumn));
                }
                decimalPlaces++;
                getNextCharacter();
            }
            double finalValue = value + fractionPart / Math.pow(10, decimalPlaces);
            currentToken = new Token(TokenType.T_DOUBLE_LITERAL, new Position(currentLine, currentColumn), finalValue);
            return true;
        }

        currentToken = new Token(TokenType.T_INT_LITERAL, new Position(currentLine, currentColumn), value);
        return true;
    }

    private boolean tryBuildString() throws IOException, UnexpectedEndOfTextException {
        getNextCharacter();
        StringBuilder sb = new StringBuilder();

        while ((Character.isISOControl(currentCharacter) || Character.isSpaceChar(currentCharacter))
            && currentCharacter != '"' && currentCharacter != -1) {
            if (currentCharacter == '\\') {
                getNextCharacter();
                switch ((char) currentCharacter) {
                    case 't' -> sb.append('\t');
                    case 'b' -> sb.append('\b');
                    case 'r' -> sb.append('\r');
                    case 'n' -> sb.append('\n');
                    case '\"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append('\\').append(currentCharacter);
                }
            } else {
                sb.append(currentCharacter);
                getNextCharacter();
            }
        }

        if (currentCharacter == '"') {
            currentToken = new Token(TokenType.T_STRING_LITERAL, new Position(currentLine, currentColumn), sb.toString());
            return true;
        } else {
            throw new UnexpectedEndOfTextException(String.format("Unexpected end of text occurred L:%d, C:%d", currentLine, currentColumn));
        }
    }

    private boolean tryBuildIdentifierOrKeyword() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(currentCharacter);
        getNextCharacter();

        while(Character.isLetterOrDigit(currentCharacter) || currentCharacter == '_') {
            sb.append(currentCharacter);
        }

        if (sb.toString().length() == 0) return false;
        var tokenType = getTokenTypeFromString(sb.toString());
        currentToken = new Token(tokenType, new Position(currentLine, currentColumn), sb.toString());
        return true;
    }

    private TokenType getTokenTypeFromString(String value) {
        return switch (value) {
            case "and" -> TokenType.T_AND_OP;
            case "or" -> TokenType.T_OR_OP;
            case "as" -> TokenType.T_AS_OP;
            case "is" -> TokenType.T_IS_OP;
            case "bool", "int", "double" -> TokenType.T_TYPE;
            case "true", "false" -> TokenType.T_BOOL_LITERAL;
            case "string" -> TokenType.T_STRING_TYPE;
            case "break" -> TokenType.T_BREAK;
            case "continue" -> TokenType.T_CONTINUE;
            case "if" -> TokenType.T_IF;
            case "else" -> TokenType.T_ELSE;
            case "func" -> TokenType.T_FUNC_KEYWORD;
            case "match" -> TokenType.T_MATCH;
            case "mutable" -> TokenType.T_MUTABLE;
            case "_" -> TokenType.T_UNDERSCORE;
            case "return" -> TokenType.T_RETURN;
            case "void" -> TokenType.T_VOID_TYPE;
            case "default" -> TokenType.T_DEFAULT;
            case "while" -> TokenType.T_WHILE;
            case "null" -> TokenType.T_NULL_LITERAL;
            default -> TokenType.T_IDENTIFIER;
        };
    }

    private boolean tryBuildSymbol() throws IOException {
        boolean result = true;
        char firstChar = (char) currentCharacter;
        getNextCharacter();

        if (firstChar == '+') {
            buildToken(TokenType.T_ADD_OP, String.valueOf(firstChar));
        } else if (firstChar == '-') {
            buildToken(TokenType.T_SUB_OP, String.valueOf(firstChar));
        } else if (firstChar == '*') {
            buildToken(TokenType.T_MUL_OP, String.valueOf(firstChar));
        } else if (firstChar == ':') {
            buildToken(TokenType.T_COLON, String.valueOf(firstChar));
        } else if (firstChar == ';') {
            buildToken(TokenType.T_SEMICOLON, String.valueOf(firstChar));
        } else if (firstChar == ',') {
            buildToken(TokenType.T_COMMA, String.valueOf(firstChar));
        } else if (firstChar == '{') {
            buildToken(TokenType.T_CURLY_OPEN, String.valueOf(firstChar));
        } else if (firstChar == '}') {
            buildToken(TokenType.T_CURLY_CLOSE, String.valueOf(firstChar));
        } else if (firstChar == '%') {
            buildToken(TokenType.T_MOD_OP, String.valueOf(firstChar));
        } else if (firstChar == '(') {
            buildToken(TokenType.T_PAREN_OPEN, String.valueOf(firstChar));
        } else if (firstChar == ')') {
            buildToken(TokenType.T_PAREN_CLOSE, String.valueOf(firstChar));
        } else if (firstChar == '/') {
            if (currentCharacter == '/') {
                buildToken(TokenType.T_DIV_INT_OP, String.valueOf(firstChar + (char) currentCharacter));
                getNextCharacter();
            } else buildToken(TokenType.T_DIV_OP, String.valueOf(firstChar));
        } else if (firstChar == '=') {
            if (currentCharacter == '>') {
                buildToken(TokenType.T_ARROW, String.valueOf(firstChar + (char) currentCharacter));
                getNextCharacter();
            } else if (currentCharacter == '=') {
                buildToken(TokenType.T_EQUAL_OP, String.valueOf(firstChar + (char) currentCharacter));
                getNextCharacter();
            } else buildToken(TokenType.T_ASSIGNMENT_OP, String.valueOf(firstChar));
        } else if (firstChar == '>') {
            if (currentCharacter == '=') {
                buildToken(TokenType.T_GE_OP, String.valueOf(firstChar + (char) currentCharacter));
                getNextCharacter();
            } else buildToken(TokenType.T_GT_OP, String.valueOf(firstChar));
        } else if (firstChar == '<') {
            if (currentCharacter == '=') {
                buildToken(TokenType.T_LE_OP, String.valueOf(firstChar + (char) currentCharacter));
                getNextCharacter();
            } else buildToken(TokenType.T_LT_OP, String.valueOf(firstChar));
        } else if (firstChar == '!') {
            if (currentCharacter == '=') {
                buildToken(TokenType.T_NOT_EQUAL_OP, String.valueOf(firstChar + (char) currentCharacter));
                getNextCharacter();
            } else buildToken(TokenType.T_UNARY_OP, String.valueOf(firstChar));
        } else if (firstChar == '?') {
            if (currentCharacter == '?') {
                buildToken(TokenType.T_NULL_COMP_OP, String.valueOf(firstChar + (char) currentCharacter));
                getNextCharacter();
            } else buildToken(TokenType.T_TYPE_OPT, String.valueOf(firstChar));
        } else {
            result = false;
        }
        return result;
    }

    private void buildToken(TokenType type, String value) {
        currentToken = new Token(type, new Position(currentLine, currentColumn), value);
    }


}
