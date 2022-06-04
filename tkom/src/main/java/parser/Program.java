package parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import parser.statements.Statement;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Program {

    private final List<Statement> statements;

}
