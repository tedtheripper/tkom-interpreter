package parser.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.Parameter;
import parser.Type;
import semcheck.Visitor;
import semcheck.exception.SemCheckException;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class FunctionDef implements Statement{

    private String name;
    private Type functionReturnType;
    private List<Parameter> parameterList;
    private List<Statement> statementsBlock;

    @Override
    public void accept(Visitor visitor) throws SemCheckException {
        visitor.visitFunctionDef(this);
    }
}
