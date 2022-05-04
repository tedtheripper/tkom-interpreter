package lexer;

import lexer.exception.*;
import lexer.utils.LexerMappingUtils;

import java.io.IOException;
import java.io.PushbackReader;

public class Tokenizer {

    private final static int DOUBLE_NUMBERS_OF_PRECISION = 16;
    private final static char DECIMAL_POINT = '.';
    private final static int END_OF_SOURCE = -1;

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
                omitCommentTillEndOfLine();
            }
            if (currentCharacter == '\n') {
                currentLine++;
                currentColumn = 0;
            }
            if (!hasSourceEnded())
                getNextCharacter();
        }

        if (hasSourceEnded()) return new Token(TokenType.T_ETX, new Position(currentLine, currentColumn), null);

        Token currentToken;
        if ((currentToken = tryBuildNumber()) != null
        || (currentToken = tryBuildString()) != null
        || (currentToken = tryBuildMultipleCharacterSymbol()) != null
        || (currentToken = tryBuildIdentifierOrKeyword()) != null) {
            return currentToken;
        } else {
            throw new InvalidTokenException(
                    String.format("Invalid token found at L:%d, C:%d", currentLine, currentColumn),
                    currentLine, currentColumn);
        }
    }

    private void omitCommentTillEndOfLine() throws IOException {
        while(currentCharacter != '\n' && !hasSourceEnded()) {
            getNextCharacter();
        }
    }

    private Token tryBuildNumber() throws IOException, IntegerOverflowException, DoubleOverflowException, InvalidTokenException {
        if (!Character.isDigit(currentCharacter) && currentCharacter != DECIMAL_POINT) return null;

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
            return new Token(TokenType.T_DOUBLE_LITERAL,
                    new Position(currentLine, startColumn), finalValue);
        }

        if (Character.isLetter(currentCharacter)) {
            throw new InvalidTokenException("Error while creating integer literal", currentLine, startColumn);
        }
        if (value == 0) {
            getNextCharacter();
        }
        return new Token(TokenType.T_INT_LITERAL, new Position(currentLine, startColumn), value);
    }

    private Token tryBuildString() throws IOException, UnexpectedEndOfStringException, UnexpectedEndOfTextException {
        if (currentCharacter != '"') return null;
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
            getNextCharacter();
            return buildTokenWithTValue(TokenType.T_STRING_LITERAL, stringLiteralBeginningColumn, sb.toString());
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

    private Token tryBuildIdentifierOrKeyword() throws IOException {
        var startColumn = currentColumn;
        StringBuilder sb = new StringBuilder();
        if (!Character.isLetterOrDigit(currentCharacter) && !(currentCharacter == '_')
                && !LexerMappingUtils.isSymbolicKeyword(String.valueOf((char)currentCharacter))) return null;
        sb.append((char)currentCharacter);
        getNextCharacter();

        if (LexerMappingUtils.isSymbolicKeyword(sb.toString())) {
            return buildTokenWithTValue(getTokenTypeFromString(sb.toString()), startColumn, sb.toString());
        }

        while(Character.isLetterOrDigit(currentCharacter) || currentCharacter == '_') {
            sb.append((char)currentCharacter);
            getNextCharacter();
        }

        if (sb.toString().length() == 0) return null;
        var tokenType = getTokenTypeFromString(sb.toString());
        if (tokenType.equals(TokenType.T_BOOL_LITERAL)) {
            return buildTokenWithTValue(tokenType, startColumn, Boolean.valueOf(sb.toString()));
        } else {
            return buildTokenWithTValue(tokenType, startColumn, sb.toString());
        }
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

    private Token tryBuildMultipleCharacterSymbol() throws IOException {
        char firstChar = (char) currentCharacter;
        TokenType tokenType;
        var startColumn = currentColumn;
        try {
            var opValue = String.valueOf(firstChar);
            tokenType = TokenType.fromString(opValue);
            if (!LexerMappingUtils.isSymbolicOperator(opValue)) return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
        getNextCharacter();
        try {
            var multipleCharacterSymbol = "" + firstChar + (char) currentCharacter;
            tokenType = TokenType.fromString(multipleCharacterSymbol);
            if (!LexerMappingUtils.isSymbolicOperator(multipleCharacterSymbol)) return null;
            getNextCharacter();
            return buildTokenWithTValue(tokenType, startColumn, multipleCharacterSymbol);
        } catch (IllegalArgumentException e) {
            return buildTokenWithTValue(tokenType, startColumn, String.valueOf(firstChar));
        }
    }

    private <T> Token buildTokenWithTValue(TokenType type, long startColumn, T value) {
        return new Token(type, new Position(currentLine, startColumn), value);
    }

    private boolean hasSourceEnded() {
        return currentCharacter == END_OF_SOURCE;
    }

}
