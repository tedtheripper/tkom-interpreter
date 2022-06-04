package executor.ir;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Type {

    private boolean isNullable;
    private String typeName;

    public Type(parser.Type parserType) {
        if (parserType != null) {
            this.isNullable = parserType.isNullable();
            this.typeName = parserType.getTypeName();
        }
    }
}
