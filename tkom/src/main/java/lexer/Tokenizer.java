package lexer;

import lexer.exception.DoubleOverflowException;
import lexer.exception.IntegerOverflowException;
import lexer.exception.UnexpectedEndOfTextException;
import lexer.exception.UnknownEscapeCharacterException;

import java.io.IOException;
import java.io.PushbackReader;

public class Tokenizer {

    private int currentCharacter;
    private Token currentToken;

    private long currentLine;
    private long currentColumn;

    private PushbackReader fileSourceReader;

    private String source;
    private int stringSourceIterator;

    public Tokenizer(PushbackReader fileSourceReader) {
        this.fileSourceReader = fileSourceReader;
    }

    public Tokenizer(String source) {
        this.source = source;
        this.stringSourceIterator = 0;
    }

    public void getNextCharacter() throws IOException {
        if (this.fileSourceReader != null) {
            currentCharacter = this.fileSourceReader.read();
        } else {
            currentCharacter = stringSourceIterator != source.length() ? source.charAt(stringSourceIterator++) : -1;
        }
    }

    public Token getNextToken() throws IOException {
        getNextCharacter();

        while (Character.isWhitespace(currentCharacter)) {
            getNextCharacter();
        }

        if (currentCharacter == -1) return new Token(TokenType.T_ETX, new Position(currentLine, currentColumn), null);

        switch ((char) currentCharacter) {
            // TODO
        }
        return null;
    }

    public boolean tryBuildNumber() throws IOException, IntegerOverflowException, DoubleOverflowException {
        if (!Character.isDigit(currentCharacter) && currentCharacter != '.' && currentCharacter != '-') return false;
        boolean isNegative = currentCharacter == '-';
        if (isNegative) getNextCharacter();

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
            if (isNegative) finalValue = -finalValue;
            currentToken = new Token(TokenType.T_DOUBLE_LITERAL, new Position(currentLine, currentColumn), finalValue);
            return true;
        }

        if (isNegative) value = -value;
        currentToken = new Token(TokenType.T_INT_LITERAL, new Position(currentLine, currentColumn), value);
        return true;
    }

    public boolean tryBuildString() throws IOException, UnexpectedEndOfTextException, UnknownEscapeCharacterException {
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

    public boolean tryBuildIdentifierOrKeyword() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(currentCharacter);
        getNextCharacter();

        while(Character.isLetterOrDigit(currentCharacter) || currentCharacter == '_') {
            sb.append(currentCharacter);
        }

        var tokenType = getTokenTypeFromString(sb.toString());
        currentToken = new Token(tokenType, new Position(currentLine, currentColumn), sb.toString());
        return true;
    }

    public TokenType getTokenTypeFromString(String value) {
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

    public boolean tryBuildSymbolicOperator() {
        return false;
    }


}
