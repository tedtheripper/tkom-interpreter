package executor.ir;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LiteralObject implements Object {

    private Type type;
    private java.lang.Object value;
}
