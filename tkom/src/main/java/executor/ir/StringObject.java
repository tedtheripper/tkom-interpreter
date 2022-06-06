package executor.ir;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StringObject implements ExecutorObject, Comparable<StringObject> {
    private String value;

    public StringObject(StringObject o) {
        this.value = o.getValue();
    }

    @Override
    public int compareTo(StringObject o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public ExecutorObject copy(ExecutorObject o) {
        return new StringObject((StringObject) o);
    }
}
