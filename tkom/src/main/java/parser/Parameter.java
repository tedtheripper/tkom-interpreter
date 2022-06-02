package parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Parameter {
    private final boolean isMutable;
    private final Type type;
    private final String identifier;

}
