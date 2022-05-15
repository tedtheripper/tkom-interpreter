package parser;

import parser.statements.FunctionDef;
import parser.statements.Statement;

import java.util.List;
import java.util.Map;

public class Program {

    private final Map<String, FunctionDef> functions;
    private final List<Statement> statements;

    public Program(Map<String, FunctionDef> functions, List<Statement> statements) {
        this.functions = functions;
        this.statements = statements;
    }


}
