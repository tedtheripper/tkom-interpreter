package parser.statements;

import parser.Parameter;
import parser.Type;

import java.util.List;

public class FunctionDef implements Statement{

    private String name;

    private Type functionReturnType;
    private List<Parameter> parameterList;
    private List<Statement> statementsBlock;
    public FunctionDef(String name, Type type, List<Parameter> parameters, List<Statement> statementsBlock) {
        this.name = name;
        this.functionReturnType = type;
        this.parameterList = parameters;
        this.statementsBlock = statementsBlock;
    }

    public String getName() {
        return name;
    }
}
