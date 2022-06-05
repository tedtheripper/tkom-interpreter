package executor.ir;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BooleanObject implements ExecutorObject, Comparable<BooleanObject> {

    private boolean value;

    public BooleanObject(BooleanObject o) {
        this.value = o.isValue();
    }

    @Override
    public int compareTo(BooleanObject o) {
        return Boolean.compare(this.value, o.value);
    }

    @Override
    public ExecutorObject copy(ExecutorObject o) {
        return new BooleanObject((BooleanObject) o);
    }
}
