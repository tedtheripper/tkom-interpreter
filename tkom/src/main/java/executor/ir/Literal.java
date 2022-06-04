package executor.ir;

public class Literal {

    private Type type;
    private Object value;

    public Literal(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
}
