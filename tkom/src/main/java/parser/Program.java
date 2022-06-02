package parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.statements.FunctionDef;
import parser.statements.Statement;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Program {

    private final Map<String, FunctionDef> functions;
    private final List<Statement> statements;

}
