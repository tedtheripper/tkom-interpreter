package executor.stdlib;

import executor.ir.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StdLibImpl {

    private final Map<String, Function> embeddedFunctionsDefinitions = new HashMap<>();

    public StdLibImpl() {
        buildStandardLibrary();
    }

    public boolean hasFunction(String name) {
        return embeddedFunctionsDefinitions.containsKey(name);
    }

    public Function getFunction(String name) {
        return embeddedFunctionsDefinitions.get(name);
    }

    public Map<String, Function> getEmbeddedFunctionsDefinitions() {
        return new HashMap<>(this.embeddedFunctionsDefinitions);
    }

    public void usePrint(String text) {
        System.out.println(text);
    }

    public String useGetInput() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    private void buildStandardLibrary() {
        var printScope = new Scope();
        printScope.addVariable(new Variable("data", new Type(true, "string"), false));
        embeddedFunctionsDefinitions.put("print", new LibFunction("print", printScope, new Type(false, "void")));
        embeddedFunctionsDefinitions.put("get_input", new LibFunction("get_input", new Scope(), new Type(false, "string")));

    }


}
