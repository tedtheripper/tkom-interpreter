package executor.ir;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DoubleObject implements ExecutorObject, Comparable<DoubleObject> {
    private double value;

    public DoubleObject(DoubleObject o) {
        this.value = o.getValue();
    }

    @Override
    public int compareTo(DoubleObject o) {
        return Double.compare(this.value, o.value);
    }

    @Override
    public ExecutorObject copy(ExecutorObject o) {
        return new DoubleObject((DoubleObject) o);
    }
}
