package executor.ir;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IntegerObject implements ExecutorObject, Comparable<IntegerObject> {
    private int value;

    public IntegerObject(IntegerObject o) {
        this.value = o.getValue();
    }

    @Override
    public int compareTo(IntegerObject o) {
        return Integer.compare(this.value, o.value);
    }

    @Override
    public ExecutorObject copy(ExecutorObject o) {
        return new IntegerObject((IntegerObject) o);
    }
}
