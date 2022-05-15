package parser;

import common.Position;
import lexer.Token;
import lexer.TokenType;
import lexer.Tokenizer;
import lexer.exception.LexerException;
import parser.exception.*;
import parser.expressions.*;
import parser.statements.*;
import parser.utils.OperatorUtils;
import source_loader.exception.SourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    private final Tokenizer tokenizer;

    private Token currentToken;

    public Parser(Tokenizer tokenizer) throws LexerException, IOException, SourceException {
        this.tokenizer = tokenizer;
        this.currentToken = tokenizer.getNextToken();
    }

    public void getNextToken() throws LexerException, IOException, SourceException {
        this.currentToken = this.tokenizer.getNextToken();
    }

    public Program parse() throws IOException, LexerException, SourceException, SyntaxException {

        Map<String, FunctionDef> functions = new HashMap<>();
        List<Statement> statements = new ArrayList<>();
        Statement programStatement;
        while((programStatement = tryParseStatementOrFunctionDef()) != null) {
            if (programStatement instanceof FunctionDef funcDef) {
                functions.put(funcDef.getName(), funcDef);
            } else {
                statements.add(programStatement);
            }
        }
        if (!checkAndConsume(TokenType.T_ETX)) {
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_ETX);
        }
        return new Program(functions, statements);
    }

    private Statement tryParseStatementOrFunctionDef() throws IOException, LexerException, SourceException, SyntaxException {
        Statement statement;
        if (checkAndConsume(TokenType.T_FUNC_KEYWORD)) {
            statement = tryParseFunctionDefinition();
        } else {
            statement = tryParseStatement();
        }
        return statement;
    }

    // functionDef = "func", identifier, "(", [parametersList], ")", ":", type, "{", statementBlock, "}" ;
    private FunctionDef tryParseFunctionDefinition() throws IOException, LexerException, SourceException, SyntaxException {

        if(!currentToken.is(TokenType.T_IDENTIFIER))
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_IDENTIFIER);

        var name = currentToken.value().toString();
        getNextToken();
        checkConsumeOrThrow(TokenType.T_PAREN_OPEN);

        var parameters = tryParseParameters();
        checkConsumeOrThrow(TokenType.T_PAREN_CLOSE);
        checkConsumeOrThrow(TokenType.T_COLON);

        var type = tryParseType();
        if (type == null)
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_TYPE);
        checkConsumeOrThrow(TokenType.T_CURLY_OPEN);
        var statementsBlock = tryParseStatementsBlock();
        if (statementsBlock.isEmpty())
            throwMissingStatementBlockException(currentToken.position());
        checkConsumeOrThrow(TokenType.T_CURLY_CLOSE);

        return new FunctionDef(name, type, parameters, statementsBlock);
    }

    // type                 = nonNullableType, ["?"]
    //                      | "void"
    //                      | "null" ;
    private Type tryParseType() throws IOException, LexerException, SourceException {
        boolean isNullable = false;
        if (!currentToken.is(TokenType.T_TYPE)
                && !currentToken.is(TokenType.T_NULL_LITERAL)
                && !currentToken.is(TokenType.T_VOID_TYPE))
            return null;
        var typeName = currentToken.value().toString();
        getNextToken();
        if (!typeName.equals(TokenType.T_VOID_TYPE.getText()) && !typeName.equals(TokenType.T_NULL_LITERAL.getText()))
            isNullable = checkAndConsume(TokenType.T_TYPE_OPT);
        return new Type(isNullable, typeName);
    }

    // statementBlock        = statement, {statement} ;
    private List<Statement> tryParseStatementsBlock() throws IOException, LexerException, SourceException, SyntaxException {
        List<Statement> statementList = new ArrayList<>();
        var statement = tryParseStatement();
        if (statement == null)
            return statementList;
        statementList.add(statement);

        while((statement = tryParseStatement()) != null) {
            statementList.add(statement);
        }
        return statementList;
    }

    // parametersList = parameter, {",", parameter} ;
    private List<Parameter> tryParseParameters() throws IOException, LexerException, SourceException, SyntaxException {
        List<Parameter> parameterList = new ArrayList<>();

        var parameter = tryParseParameter();
        if (parameter == null)
            return parameterList;
        parameterList.add(parameter);

        while(checkAndConsume(TokenType.T_COMMA)) {
            parameter = tryParseParameter();
            if (parameter == null)
                throwMissingParameterException(currentToken.position());
            parameterList.add(parameter);
        }

        return parameterList;
    }

    // parameter = type, identifier ;
    private Parameter tryParseParameter() throws IOException, LexerException, SourceException, SyntaxException {
        var type = tryParseType();
        if (type == null)
            return null;

        if (!currentToken.is(TokenType.T_IDENTIFIER))
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_IDENTIFIER);
        var identifier = currentToken.value().toString();
        getNextToken();

        return new Parameter(type, identifier);
    }

    // statement            = conditionalStatement
    //                      | simpleStatement, ";"
    //                      | variableDeclaration, ";" ;
    private Statement tryParseStatement() throws IOException, LexerException, SourceException, SyntaxException {
        Statement statement;
        if ((statement = tryParseConditionalStatement()) != null
        || (statement = tryParseSimpleStatement()) != null
        || (statement = tryParseVariableDeclaration()) != null) {
            var eligibleForSemicolonCheck = !(statement instanceof IfStatement
                    || statement instanceof WhileStatement || statement instanceof MatchStatement);

            if (eligibleForSemicolonCheck && !checkAndConsume(TokenType.T_SEMICOLON))
                throwMissingSemicolonException(currentToken.position());
            return statement;
        }
        return null;
    }

    // conditionalStatement = ifStatement
    //                      | whileStatement
    //                      | matchStatement;
    private Statement tryParseConditionalStatement() throws IOException, LexerException, SourceException, SyntaxException {
        Statement conditionalStatement;
        if ((conditionalStatement = tryParseIfStatement()) != null
        || (conditionalStatement = tryParseWhileStatement()) != null
        || (conditionalStatement = tryParseMatchStatement()) != null)
            return conditionalStatement;
        return null;
    }

    // simpleStatement      = assignment
    //                      | functionCall
    //                      | returnStatement
    //                      | "break"
    //                      | "continue" ;
    private Statement tryParseSimpleStatement() throws IOException, LexerException, SourceException, SyntaxException {
        Statement simpleStatement;
        if ((simpleStatement = tryParseExpression()) != null
        || (simpleStatement = tryParseReturnStatement()) != null
        || (simpleStatement = tryParseJumpLoopStatement()) != null) {
            return simpleStatement;
        }
        return null;
    }

    // "break" | "continue"
    private JumpLoopStatement tryParseJumpLoopStatement() throws IOException, LexerException, SourceException {
        JumpLoopStatement statement = null;
        if (currentToken.is(TokenType.T_BREAK) || currentToken.is(TokenType.T_CONTINUE)) {
            statement = new JumpLoopStatement(currentToken.value().toString());
            getNextToken();
        }
        return statement;
    }

    // returnStatement = "return", [expression] ;
    private ReturnStatement tryParseReturnStatement() throws IOException, LexerException, SourceException, SyntaxException {
        if (!checkAndConsume(TokenType.T_RETURN))
            return null;
        var expression = tryParseExpression();
        return new ReturnStatement(expression);
    }

    // argumentList = expression, {",", expression} ;
    private List<Expression> tryParseArgumentList() throws IOException, LexerException, SourceException, SyntaxException {
        List<Expression> argumentList = new ArrayList<>();
        Expression expression = tryParseExpression();
        if (expression == null)
            return argumentList;
        argumentList.add(expression);

        while(checkAndConsume(TokenType.T_COMMA)) {
            expression = tryParseExpression();
            if (expression == null)
                throwMissingExpressionException(currentToken.position());
            argumentList.add(expression);
        }

        return argumentList;
    }

    // variableDeclaration = ["mutable"], type, identifier, assignmentOp, (expression) ;
    private VariableDeclarationStatement tryParseVariableDeclaration() throws IOException, LexerException, SourceException, SyntaxException {
        var isMutable = checkAndConsume(TokenType.T_MUTABLE);
        var type = tryParseType();
        if (isMutable && type == null) {
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_TYPE);
        } else if (type == null)
            return null;

        if (!currentToken.is(TokenType.T_IDENTIFIER))
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_IDENTIFIER);
        var name = currentToken.value().toString();
        getNextToken();
        checkConsumeOrThrow(TokenType.T_ASSIGNMENT_OP);
        var expression = tryParseExpression();
        if (expression == null)
            throwMissingExpressionException(currentToken.position());

        return new VariableDeclarationStatement(isMutable, type, name, expression);
    }

    // ifStatement = ifBlock, [elseBlock] ;
    private IfStatement tryParseIfStatement() throws IOException, LexerException, SourceException, SyntaxException {
        var ifBlock = tryParseIfBlock();
        if (ifBlock == null)
            return null;

        var elseBlock = tryParseElseBlock();

        return new IfStatement(ifBlock, elseBlock);
    }

    // ifBlock = "if", "(", expression, ")", "{", {statementBlock}, "}" ;
    private IfBlock tryParseIfBlock() throws IOException, LexerException, SourceException, SyntaxException {
        if (!checkAndConsume(TokenType.T_IF))
            return null;
        checkConsumeOrThrow(TokenType.T_PAREN_OPEN);
        var expression = tryParseExpression();
        if (expression == null)
            throwMissingExpressionException(currentToken.position());

        checkConsumeOrThrow(TokenType.T_PAREN_CLOSE);
        checkConsumeOrThrow(TokenType.T_CURLY_OPEN);

        var statementList = tryParseStatementsBlock();

        checkConsumeOrThrow(TokenType.T_CURLY_CLOSE);

        return new IfBlock(expression, statementList);
    }

    // elseBlock = "else", "{", {statementBlock}, "}" ;
    private ElseBlock tryParseElseBlock() throws IOException, LexerException, SourceException, SyntaxException {
        if (!checkAndConsume(TokenType.T_ELSE))
            return null;
        checkConsumeOrThrow(TokenType.T_CURLY_OPEN);

        var statementList = tryParseStatementsBlock();

        checkConsumeOrThrow(TokenType.T_CURLY_CLOSE);

        return new ElseBlock(statementList);
    }

    // whileStatement = "while", "(", expression, ")", "{", {statementBlock}, "}" ;
    private WhileStatement tryParseWhileStatement() throws IOException, LexerException, SourceException, SyntaxException {
        if (!checkAndConsume(TokenType.T_WHILE))
            return null;
        checkConsumeOrThrow(TokenType.T_PAREN_OPEN);

        var expression = tryParseExpression();
        if (expression == null)
            throwMissingExpressionException(currentToken.position());

        checkConsumeOrThrow(TokenType.T_PAREN_CLOSE);

        checkConsumeOrThrow(TokenType.T_CURLY_OPEN);

        var statementList = tryParseStatementsBlock();

        checkConsumeOrThrow(TokenType.T_CURLY_CLOSE);

        return new WhileStatement(expression, statementList);
    }

    // matchStatement = "match", "(", expression, ")", "{", insideMatchStatement, {insideMatchStatement}. "}" ;
    private MatchStatement tryParseMatchStatement() throws IOException, LexerException, SourceException, SyntaxException {
        if (!checkAndConsume(TokenType.T_MATCH))
            return null;
        checkConsumeOrThrow(TokenType.T_PAREN_OPEN);
        var expression = tryParseExpression();
        if (expression == null)
            throwMissingExpressionException(currentToken.position());
        checkConsumeOrThrow(TokenType.T_PAREN_CLOSE);
        checkConsumeOrThrow(TokenType.T_CURLY_OPEN);

        List<Statement> matchStatements = new ArrayList<>();
        InsideMatchStatement statement;
        while((statement = tryParseInsideMatchStatement()) != null) {
            matchStatements.add(statement);
        }
        if (matchStatements.isEmpty())
            throwMissingStatementException(currentToken.position());

        checkConsumeOrThrow(TokenType.T_CURLY_CLOSE);

        return new MatchStatement(expression, matchStatements);
    }

    // insideMatchStatement  = (insideMatchExpression, {(orOp | andOp), insideMatchExpression} | "default"), "=>" simpleStatement, "," ;
    private InsideMatchStatement tryParseInsideMatchStatement() throws IOException, LexerException, SourceException, SyntaxException {
        boolean isDefault = false;
        Expression leftExpression = null;
        if (checkAndConsume(TokenType.T_DEFAULT))
            isDefault = true;
        else {
            leftExpression = tryParseInsideMatchExpression();
            if (leftExpression == null)
                return null;
            while(currentToken.is(TokenType.T_OR_OP) || currentToken.is(TokenType.T_AND_OP)) {
                var foundOperator = currentToken.type();
                getNextToken();
                var rightExpression = tryParseInsideMatchExpression();
                if (rightExpression == null)
                    throwMissingExpressionException(currentToken.position());
                if (foundOperator.equals(TokenType.T_AND_OP)) {
                    leftExpression = new AndExpression(leftExpression, rightExpression);
                } else {
                    leftExpression = new OrExpression(leftExpression, rightExpression);
                }
            }
        }
        checkConsumeOrThrow(TokenType.T_ARROW);
        var statement = tryParseSimpleStatement();
        if (statement == null)
            throwMissingStatementException(currentToken.position());
        checkConsumeOrThrow(TokenType.T_COMMA);
        
        return new InsideMatchStatement(isDefault, leftExpression, statement);
    }

    // insideMatchExpression = comparisonOp, valueLiteral
    //                       | expression
    //                       | "is", type;
    private Expression tryParseInsideMatchExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var expression = tryParseExpression();
        if (expression != null)
            return expression;
        if (checkAndConsume(TokenType.T_IS_OP)) {
            var type = tryParseType();
            if (type == null)
                throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_TYPE);
            return new InsideMatchTypeExpression(type);
        } else if (OperatorUtils.isCompOp(currentToken.type())) {
            var foundOperator = currentToken.value().toString();
            getNextToken();
            if (isLiteral(currentToken.type())) {
                var literal = switch (currentToken.type()) {
                    case T_BOOL_LITERAL -> new BooleanLiteralExpression((boolean)currentToken.value());
                    case T_INT_LITERAL -> new IntegerLiteralExpression((int)currentToken.value());
                    case T_DOUBLE_LITERAL -> new DoubleLiteralExpression((double)currentToken.value());
                    case T_STRING_LITERAL -> new StringLiteralExpression(currentToken.value().toString());
                    case T_NULL_LITERAL -> new NullLiteralExpression();
                    default -> null;
                };
                getNextToken();
                return new InsideMatchCompExpression(literal, new Operator(foundOperator));
            }
            throwMissingExpressionException(currentToken.position());
        }
        return null;
    }

    // expression = nullCheckExpression, ["=", nullCheckExpression] ;
    private Expression tryParseExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseNullCheckExpression();
        if (leftExpression == null)
            return null;
        if (!checkAndConsume(TokenType.T_ASSIGNMENT_OP))
            return leftExpression;
        var rightExpression = tryParseNullCheckExpression();
        if (rightExpression == null)
            throwMissingExpressionException(currentToken.position());
        return new AssignmentExpression(leftExpression, rightExpression);
    }

    // nullCheckExpression   = orExpression, {"??", orExpression} ;
    private Expression tryParseNullCheckExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseOrExpression();
        if (leftExpression == null)
            return null;
        while(checkAndConsume(TokenType.T_NULL_COMP_OP)) {
            var rightExpression = tryParseOrExpression();
            if (rightExpression == null)
                throwMissingExpressionException(currentToken.position());
            leftExpression = new NullCheckExpression(leftExpression, rightExpression);
        }
        return leftExpression;
    }

    // orExpression          = andExpression, {orOp, andExpression} ;
    private Expression tryParseOrExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseAndExpression();
        if (leftExpression == null)
            return null;
        while(checkAndConsume(TokenType.T_OR_OP)) {
            var rightExpression = tryParseAndExpression();
            if (rightExpression == null)
                throwMissingExpressionException(currentToken.position());
            leftExpression = new OrExpression(leftExpression, rightExpression);
        }
        return leftExpression;
    }

    // andExpression         = compExpression, {andOp, compExpression} ;
    private Expression tryParseAndExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseCompExpression();
        if (leftExpression == null)
            return null;
        while(checkAndConsume(TokenType.T_AND_OP)) {
            var rightExpression = tryParseCompExpression();
            if (rightExpression == null)
                throwMissingExpressionException(currentToken.position());
            leftExpression = new AndExpression(leftExpression, rightExpression);
        }
        return leftExpression;
    }

    // compExpression        = asExpression, [comparisonOp, asExpression] ;
    private Expression tryParseCompExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseIsAsExpression();
        if (leftExpression == null)
            return null;
        if (!OperatorUtils.isCompOp(currentToken.type()))
            return leftExpression;
        var savedType = currentToken.type();
        getNextToken();
        var rightExpression = tryParseIsAsExpression();
        if (rightExpression == null)
            throwMissingExpressionException(currentToken.position());
        return new CompExpression(leftExpression, rightExpression, new Operator(savedType.getText()));
    }

    // isasExpression        = addExpression, [("is" | "as"), type] ;
    private Expression tryParseIsAsExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseAdditiveExpression();
        if (leftExpression == null)
            return null;
        if (!OperatorUtils.isIsAsOp(currentToken.type()))
            return leftExpression;
        var foundOperator = currentToken.type();
        getNextToken();
        var type = tryParseType();
        if (type == null)
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), TokenType.T_TYPE);

        var operator = new Operator(foundOperator.getText());
        return new IsAsExpression(leftExpression, type, operator);
    }

    // addExpression         = mulExpression, {additiveOp, mulExpression} ;
    private Expression tryParseAdditiveExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseMultiplicativeExpression();
        if (leftExpression == null)
            return null;
        while(OperatorUtils.isAddOp(currentToken.type())) {
            var foundType = currentToken.type();
            getNextToken();
            var rightExpression = tryParseMultiplicativeExpression();
            if (rightExpression == null)
                throwMissingExpressionException(currentToken.position());
            if (foundType.equals(TokenType.T_ADD_OP)) {
                leftExpression = new AddExpression(leftExpression, rightExpression);
            } else {
                leftExpression = new SubExpression(leftExpression, rightExpression);
            }
        }
        return leftExpression;
    }

    // mulExpression         = unaryExpression, {multiOp, unaryExpression} ;
    private Expression tryParseMultiplicativeExpression() throws IOException, LexerException, SourceException, SyntaxException {
        var leftExpression = tryParseUnaryExpression();
        if (leftExpression == null)
            return null;
        while(OperatorUtils.isMulOp(currentToken.type())) {
            var foundType = currentToken.type();
            getNextToken();
            var rightExpression = tryParseUnaryExpression();
            if (rightExpression == null)
                throwMissingExpressionException(currentToken.position());
            leftExpression = switch (foundType) {
                case T_MUL_OP -> new MulExpression(leftExpression, rightExpression);
                case T_DIV_OP -> new DivExpression(leftExpression, rightExpression);
                case T_DIV_INT_OP -> new DivIntExpression(leftExpression, rightExpression);
                case T_MOD_OP -> new ModExpression(leftExpression, rightExpression);
                default -> null;
            };
        }

        return leftExpression;
    }

    // unaryExpression       = [unaryOp], baseExpression ;
    private Expression tryParseUnaryExpression() throws IOException, LexerException, SourceException, SyntaxException {
        if(checkAndConsume(TokenType.T_UNARY_OP)) {
            var expression = tryParseBaseExpression();
            if (expression == null)
                 throwMissingExpressionException(currentToken.position());
            return new UnaryExpression(expression, new Operator(TokenType.T_UNARY_OP.getText()));
        } else {
            return tryParseBaseExpression();
        }
    }

    // baseExpression       = valueLiteral
    //                      | "(", expression, ")"
    //                      | functionCall
    //                      | identifier ;
    private Expression tryParseBaseExpression() throws IOException, LexerException, SourceException, SyntaxException {
        if (checkAndConsume(TokenType.T_PAREN_OPEN)) {
            var expression = tryParseExpression();
            if (expression == null)
                throwMissingExpressionException(currentToken.position());
            checkConsumeOrThrow(TokenType.T_PAREN_CLOSE);
            return expression;
        } else if (currentToken.is(TokenType.T_IDENTIFIER) || currentToken.is(TokenType.T_UNDERSCORE)) {
            var name = currentToken.value().toString();
            getNextToken();
            if (!checkAndConsume(TokenType.T_PAREN_OPEN))
                return new Identifier(name);
            else {
                var arguments = tryParseArgumentList();
                checkConsumeOrThrow(TokenType.T_PAREN_CLOSE);
                return new FunctionCallExpression(name, arguments);
            }
        } else if (isLiteral(currentToken.type())) {
            var expression = switch (currentToken.type()) {
                case T_BOOL_LITERAL -> new BooleanLiteralExpression((boolean)currentToken.value());
                case T_INT_LITERAL -> new IntegerLiteralExpression((int)currentToken.value());
                case T_DOUBLE_LITERAL -> new DoubleLiteralExpression((double)currentToken.value());
                case T_STRING_LITERAL -> new StringLiteralExpression(currentToken.value().toString());
                case T_NULL_LITERAL -> new NullLiteralExpression();
                default -> null;
            };
            getNextToken();
            return expression;
        }
        return null;
    }

    private boolean checkAndConsume(TokenType type) throws LexerException, IOException, SourceException {
        if (!currentToken.is(type))
            return false;
        else {
            currentToken = this.tokenizer.getNextToken();
            return true;
        }
    }

    private void checkConsumeOrThrow(TokenType type) throws IOException, LexerException, SourceException, UnexpectedTokenException {
        if (!checkAndConsume(type))
            throwUnexpectedTokenException(currentToken.position(), currentToken.type(), type);
    }

    private void throwUnexpectedTokenException(Position position, TokenType received, TokenType expected) throws UnexpectedTokenException {
        throw new UnexpectedTokenException(position, received, expected);
    }

    private void throwMissingExpressionException(Position position) throws MissingExpressionException {
        throw new MissingExpressionException(position);
    }

    private void throwMissingParameterException(Position position) throws MissingParameterException {
        throw new MissingParameterException(position);
    }

    private void throwMissingSemicolonException(Position position) throws MissingSemicolonException {
        throw new MissingSemicolonException(position);
    }

    private void throwMissingStatementBlockException(Position position) throws MissingStatementBlockException {
        throw new MissingStatementBlockException(position);
    }

    private void throwMissingStatementException(Position position) throws MissingStatementException {
        throw new MissingStatementException(position);
    }

    private boolean isLiteral(TokenType type) {
        var test = type.name();
        return test.contains("LITERAL");
    }



}
