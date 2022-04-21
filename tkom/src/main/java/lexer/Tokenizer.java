package lexer;

import lexer.exception.DoubleOverflowException;
import lexer.exception.IntegerOverflowException;
import lexer.exception.InvalidTokenException;
import lexer.exception.UnexpectedEndOfTextException;
import lexer.utils.LexerMappingUtils;

import java.io.IOException;
import java.io.PushbackReader;

public class Tokenizer {

    private final static int DOUBLE_NUMBERS_OF_PRECISION = 16;

    private Token currentToken;
    private int currentCharacter;

    private long currentLine;
    private long currentColumn;

    private final PushbackReader fileSourceReader;

    public Tokenizer(PushbackReader fileSourceReader) throws IOException {
        this.fileSourceReader = fileSourceReader;
        getNextCharacter();
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    public void getNextCharacter() throws IOException {
        if (this.fileSourceReader != null) {
            currentCharacter = this.fileSourceReader.read();
            currentColumn++;
        }
    }

    public Token getNextToken() throws IOException, DoubleOverflowException, IntegerOverflowException, UnexpectedEndOfTextException, InvalidTokenException {

        while (Character.isWhitespace(currentCharacter)) {
            if (currentCharacter == '\n') {
                currentLine++;
                currentColumn = 0;
            }
            getNextCharacter();
        }

        if (currentCharacter == '#') {
            getNextCharacter();
            omitComment();
        }

        if (currentCharacter == -1) return new Token(TokenType.T_ETX, new Position(currentLine, currentColumn), null);

        if (tryBuildNumber()) return currentToken;
        if (tryBuildString()) return currentToken;
        if (tryBuildMultipleCharacterSymbol()) return currentToken;
        if (tryBuildIdentifierOrKeyword()) return currentToken;

        throw new InvalidTokenException(
                String.format("Invalid token found at L:%d, C:%d", currentToken.position().line(), currentToken.position().column()),
                currentToken.position().line(), currentToken.position().column());
    }

    private void omitComment() throws IOException {
        while(currentCharacter != '\n') {
            getNextCharacter();
        }
        getNextCharacter();
        currentLine++;
        currentColumn = 1;
    }

    private boolean tryBuildNumber() throws IOException, IntegerOverflowException, DoubleOverflowException {
        if (!Character.isDigit(currentCharacter) && currentCharacter != '.') return false;

        int value = 0;
        if (Character.isDigit(currentCharacter) && currentCharacter != '0') {
            try {
                value = Math.addExact(value, (currentCharacter - '0'));
                getNextCharacter();
                while (Character.isDigit(currentCharacter)) {
                    value = Math.addExact(Math.multiplyExact(value, 10), (currentCharacter - '0'));
                    getNextCharacter();
                }
            } catch (ArithmeticException e) {
                throw new IntegerOverflowException(
                        String.format("Integer overflow found L:%d, C:%d", currentLine, currentColumn), currentLine, currentColumn);
            }
        }
        if (currentCharacter == '.') {
            long fractionPart = 0;
            int decimalPlaces = 0;
            getNextCharacter();
            while (Character.isDigit(currentCharacter)) {
                if (decimalPlaces > DOUBLE_NUMBERS_OF_PRECISION) {
                    throw new DoubleOverflowException(
                            String.format("Double overflow found L:%d, C:%d", currentLine, currentColumn), currentLine, currentColumn);
                }
                try {
                    fractionPart = Math.addExact(Math.multiplyExact(fractionPart, 10), (currentCharacter - '0'));
                } catch (ArithmeticException e) {
                    throw new DoubleOverflowException(
                            String.format("Double overflow found L:%d, C:%d", currentLine, currentColumn), currentLine, currentColumn);
                }
                decimalPlaces++;
                getNextCharacter();
            }
            double finalValue = value + fractionPart / Math.pow(10, decimalPlaces);
            currentToken = new Token(TokenType.T_DOUBLE_LITERAL,
                    new Position(currentLine, currentColumn - String.valueOf(finalValue).length()), finalValue);
            return true;
        }

        currentToken = new Token(TokenType.T_INT_LITERAL, new Position(currentLine, currentColumn - String.valueOf(value).length()), value);
        if (value == 0) {
            getNextCharacter();
        }
        return true;
    }

    private boolean tryBuildString() throws IOException, UnexpectedEndOfTextException {
        if (currentCharacter != '"') return false;
        getNextCharacter();
        StringBuilder sb = new StringBuilder();

        while ((Character.isLetterOrDigit(currentCharacter) || Character.isSpaceChar(currentCharacter) || currentCharacter == '\\')
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
                    default -> sb.append('\\').append((char)currentCharacter);
                }
                getNextCharacter();
            } else {
                sb.append((char)currentCharacter);
                getNextCharacter();
            }
        }

        if (currentCharacter == '"') {
            buildTokenWithStringValue(TokenType.T_STRING_LITERAL, sb.toString());
            getNextCharacter();
            return true;
        } else {
            throw new UnexpectedEndOfTextException(
                    String.format("Unexpected end of text occurred L:%d, C:%d", currentLine, currentColumn),
                    currentLine, currentColumn);
        }
    }

    private boolean tryBuildIdentifierOrKeyword() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char)currentCharacter);
        getNextCharacter();

        while(Character.isLetterOrDigit(currentCharacter) || currentCharacter == '_') {
            sb.append((char)currentCharacter);
            getNextCharacter();
        }

        if (sb.toString().length() == 0) return false;
        var tokenType = getTokenTypeFromString(sb.toString());
        buildTokenWithStringValue(tokenType, sb.toString());
        return true;
    }

    private TokenType getTokenTypeFromString(String value) {
        try {
            return TokenType.fromString(value);
        } catch (IllegalArgumentException e) {
            if (LexerMappingUtils.isType(value)) return TokenType.T_TYPE;
            else if (LexerMappingUtils.isBoolLiteral(value)) return TokenType.T_BOOL_LITERAL;
            else return TokenType.T_IDENTIFIER;
        }
    }

    private boolean tryBuildMultipleCharacterSymbol() throws IOException {
        char firstChar = (char) currentCharacter;
        TokenType tokenType;

        try {
            tokenType = TokenType.fromString(String.valueOf(firstChar));
        } catch (IllegalArgumentException e) {
            return false;
        }
        getNextCharacter();
        try {
            var multipleCharacterSymbol = "" + firstChar + (char) currentCharacter;
            tokenType = TokenType.fromString(multipleCharacterSymbol);
            buildTokenWithStringValue(tokenType, multipleCharacterSymbol);
            getNextCharacter();
        } catch (IllegalArgumentException e) {
            buildTokenWithStringValue(tokenType, String.valueOf(firstChar));
        }
        return true;

    }

    private void buildTokenWithStringValue(TokenType type, String value) {
        currentToken = new Token(type, new Position(currentLine, currentColumn - value.length()), value);
    }
}
