package lexer;

import lexer.exception.*;
import lexer.utils.LexerMappingUtils;

import java.io.IOException;
import java.io.PushbackReader;

public class Tokenizer {

    private final static int DOUBLE_NUMBERS_OF_PRECISION = 16;
    private final static char DECIMAL_POINT = '.';
    private final static int END_OF_SOURCE = -1;

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

    public Token getNextToken() throws IOException, DoubleOverflowException, IntegerOverflowException, UnexpectedEndOfTextException, InvalidTokenException, UnexpectedEndOfStringException {

        while (Character.isWhitespace(currentCharacter) || (char)currentCharacter == '#') {
            if (currentCharacter == '#') {
                getNextCharacter();
                omitComment();
            }
            if (currentCharacter == '\n') {
                currentLine++;
                currentColumn = 0;
            }
            if (!hasSourceEnded())
                getNextCharacter();
        }

        if (hasSourceEnded()) return new Token(TokenType.T_ETX, new Position(currentLine, currentColumn), null);

        if (tryBuildNumber()) return currentToken;
        if (tryBuildString()) return currentToken;
        if (tryBuildMultipleCharacterSymbol()) return currentToken;
        if (tryBuildIdentifierOrKeyword()) return currentToken;

        throw new InvalidTokenException(
                String.format("Invalid token found at L:%d, C:%d", currentLine, currentColumn),
                currentLine, currentColumn);
    }

    private void omitComment() throws IOException {
        while(currentCharacter != '\n' && !hasSourceEnded()) {
            getNextCharacter();
        }
    }

    private boolean tryBuildNumber() throws IOException, IntegerOverflowException, DoubleOverflowException, InvalidTokenException {
        if (!Character.isDigit(currentCharacter) && currentCharacter != DECIMAL_POINT) return false;

        int value = 0;
        long startColumn = currentColumn;
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
        if (currentCharacter == DECIMAL_POINT) {
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
            if (Character.isLetter(currentCharacter) || currentCharacter == DECIMAL_POINT) {
                throw new InvalidTokenException("Error while creating double literal", currentLine, startColumn);
            }
            double finalValue = value + fractionPart / Math.pow(10, decimalPlaces);
            currentToken = new Token(TokenType.T_DOUBLE_LITERAL,
                    new Position(currentLine, startColumn), finalValue);
            return true;
        }

        if (Character.isLetter(currentCharacter)) {
            throw new InvalidTokenException("Error while creating integer literal", currentLine, startColumn);
        }
        currentToken = new Token(TokenType.T_INT_LITERAL, new Position(currentLine, startColumn), value);
        if (value == 0) {
            getNextCharacter();
        }
        return true;
    }

    private boolean tryBuildString() throws IOException, UnexpectedEndOfStringException, UnexpectedEndOfTextException {
        if (currentCharacter != '"') return false;
        var stringLiteralBeginningColumn = currentColumn;
        getNextCharacter();
        StringBuilder sb = new StringBuilder();

        while ((Character.isLetterOrDigit(currentCharacter) || Character.isSpaceChar(currentCharacter) || isAllowedSpecialCharacter((char)currentCharacter))
            && currentCharacter != '"' && !hasSourceEnded()) {
            if (currentCharacter == '\\') {
                getNextCharacter();
                if (currentCharacter == -1) break;
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
            buildTokenWithTValue(TokenType.T_STRING_LITERAL, stringLiteralBeginningColumn, sb.toString());
            getNextCharacter();
            return true;
        } else {
            if (hasSourceEnded()) {
                throw new UnexpectedEndOfTextException(
                        String.format("Unexpected end of text occurred L:%d, C:%d", currentLine, currentColumn),
                        currentLine, currentColumn
                );
            }
            throw new UnexpectedEndOfStringException(
                    String.format("Unexpected end of string occurred L:%d, C:%d", currentLine, currentColumn),
                    currentLine, currentColumn);
        }
    }

    private boolean isAllowedSpecialCharacter(char c) {
        var str = String.valueOf(c);
        return !str.matches("[a-zA-Z0-9\n]");
    }

    private boolean tryBuildIdentifierOrKeyword() throws IOException {
        var startColumn = currentColumn;
        StringBuilder sb = new StringBuilder();
        if (!Character.isLetterOrDigit(currentCharacter) && !(currentCharacter == '_')
                && !LexerMappingUtils.isSymbolicKeyword(String.valueOf((char)currentCharacter))) return false;
        sb.append((char)currentCharacter);
        getNextCharacter();

        if (LexerMappingUtils.isSymbolicKeyword(sb.toString())) {
            buildTokenWithTValue(getTokenTypeFromString(sb.toString()), startColumn, sb.toString());
            return true;
        }

        while(Character.isLetterOrDigit(currentCharacter) || currentCharacter == '_') {
            sb.append((char)currentCharacter);
            getNextCharacter();
        }

        if (sb.toString().length() == 0) return false;
        var tokenType = getTokenTypeFromString(sb.toString());
        if (tokenType.equals(TokenType.T_BOOL_LITERAL)) {
            buildTokenWithTValue(tokenType, startColumn, Boolean.valueOf(sb.toString()));
        } else buildTokenWithTValue(tokenType, startColumn, sb.toString());
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
        var startColumn = currentColumn;
        try {
            var opValue = String.valueOf(firstChar);
            tokenType = TokenType.fromString(opValue);
            if (!LexerMappingUtils.isSymbolicOperator(opValue)) return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        getNextCharacter();
        try {
            var multipleCharacterSymbol = "" + firstChar + (char) currentCharacter;
            tokenType = TokenType.fromString(multipleCharacterSymbol);
            if (!LexerMappingUtils.isSymbolicOperator(multipleCharacterSymbol)) return false;
            getNextCharacter();
            buildTokenWithTValue(tokenType, startColumn, multipleCharacterSymbol);
        } catch (IllegalArgumentException e) {
            buildTokenWithTValue(tokenType, startColumn, String.valueOf(firstChar));
        }
        return true;

    }

    private <T> void buildTokenWithTValue(TokenType type, long startColumn, T value) {
        currentToken = new Token(type, new Position(currentLine, startColumn), value);
    }

    private boolean hasSourceEnded() {
        return currentCharacter == END_OF_SOURCE;
    }

}
