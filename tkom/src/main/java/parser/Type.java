package parser;

public class Type {

    private final boolean isNullable;
    private final String typeName;

    public Type(boolean isNullable, String typeName) {
        this.isNullable = isNullable;
        this.typeName = typeName;
    }
}
