package lexer;

import common.Position;
import lexer.exception.*;
import lexer.utils.LexerMappingUtils;
import source_loader.Source;
import source_loader.exception.SourceException;

import java.io.IOException;

public class Tokenizer {

    private final static int DOUBLE_NUMBERS_OF_PRECISION = 16;
    private final static char DECIMAL_POINT = '.';
    private final static int END_OF_SOURCE = -1;
    private final static char END_OF_LINE = '\n';

    private int currentCharacter;

    private final Source source;

    public Tokenizer(Source source) throws IOException, SourceException {
        this.source = source;
        getNextCharacter();
    }

    public void getNextCharacter() throws IOException, SourceException {
        currentCharacter = this.source.getNextCharacter();
    }

    public Token getNextToken() throws IOException, DoubleOverflowException, IntegerOverflowException, UnexpectedEndOfTextException, InvalidTokenException, UnexpectedEndOfStringException, SourceException {

        omitWhitespaceAndComments();

        var currentLine = this.source.getCurrentLine();
        var currentColumn = this.source.getCurrentColumn();
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

    private void omitWhitespaceAndComments() throws IOException, SourceException {
        while (Character.isWhitespace(currentCharacter) || (char)currentCharacter == '#') {
            if (currentCharacter == '#') {
                getNextCharacter();
                omitCommentTillEndOfLine();
            }
            if (!hasSourceEnded())
                getNextCharacter();
        }
    }

    private void omitCommentTillEndOfLine() throws IOException, SourceException {
        while(currentCharacter != END_OF_LINE && !hasSourceEnded()) {
            getNextCharacter();
        }
    }

    private Token tryBuildNumber() throws IOException, IntegerOverflowException, DoubleOverflowException, InvalidTokenException, SourceException {
        if (!Character.isDigit(currentCharacter) && currentCharacter != DECIMAL_POINT) return null;

        int value = 0;
        var startColumn = this.source.getCurrentColumn();
        var startLine = this.source.getCurrentLine();
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
                        String.format("Integer overflow found L:%d, C:%d", startLine, startColumn), startLine, startColumn);
            }
        }
        if (currentCharacter == DECIMAL_POINT) {
            long fractionPart = 0;
            int decimalPlaces = 0;
            getNextCharacter();
            while (Character.isDigit(currentCharacter)) {
                if (decimalPlaces > DOUBLE_NUMBERS_OF_PRECISION) {
                    throw new DoubleOverflowException(
                            String.format("Double overflow found L:%d, C:%d", startLine, startColumn), startLine, startColumn);
                }
                try {
                    fractionPart = Math.addExact(Math.multiplyExact(fractionPart, 10), (currentCharacter - '0'));
                } catch (ArithmeticException e) {
                    throw new DoubleOverflowException(
                            String.format("Double overflow found L:%d, C:%d", startLine, startColumn), startLine, startColumn);
                }
                decimalPlaces++;
                getNextCharacter();
            }
            if (Character.isLetter(currentCharacter) || currentCharacter == DECIMAL_POINT) {
                throw new InvalidTokenException("Error while creating double literal", startLine, startColumn);
            }
            double finalValue = value + fractionPart / Math.pow(10, decimalPlaces);
            return new Token(TokenType.T_DOUBLE_LITERAL,
                    new Position(startLine, startColumn), finalValue);
        }

        if (Character.isLetter(currentCharacter)) {
            throw new InvalidTokenException("Error while creating integer literal", startLine, startColumn);
        }
        if (value == 0) {
            getNextCharacter();
        }
        return new Token(TokenType.T_INT_LITERAL, new Position(startLine, startColumn), value);
    }

    private Token tryBuildString() throws IOException, UnexpectedEndOfStringException, UnexpectedEndOfTextException, SourceException {
        if (currentCharacter != '"') return null;
        var stringLiteralBeginningColumn = this.source.getCurrentColumn();
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
            var currentLine = this.source.getCurrentLine();
            var currentColumn = this.source.getCurrentColumn();
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

    private Token tryBuildIdentifierOrKeyword() throws IOException, SourceException {
        var startColumn = this.source.getCurrentColumn();
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

    private Token tryBuildMultipleCharacterSymbol() throws IOException, SourceException {
        char firstChar = (char) currentCharacter;
        TokenType tokenType;
        var startColumn = this.source.getCurrentColumn();
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
        return new Token(type, new Position(this.source.getCurrentLine(), startColumn), value);
    }

    private boolean hasSourceEnded() {
        return currentCharacter == END_OF_SOURCE;
    }

}
