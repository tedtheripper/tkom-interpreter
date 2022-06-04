package executor.stdlib;

import parser.statements.FunctionDef;

import java.util.HashMap;
import java.util.Map;

public class StdLibImpl {

    private final Map<String, FunctionDef> embeddedFunctions = new HashMap<>();

    public StdLibImpl() {
        buildStandardFunctions();
    }

    public boolean hasFunction(String name) {
        return embeddedFunctions.containsKey(name);
    }

    private void buildStandardFunctions() {

    }

//    private FunctionDef buildPrint() {
//        return new FunctionDef(
//                "print"
//        )
//    }


}
