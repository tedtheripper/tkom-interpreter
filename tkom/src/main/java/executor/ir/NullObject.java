package executor.ir;

public class NullObject implements ExecutorObject {
    @Override
    public ExecutorObject copy(ExecutorObject o) {
        return new NullObject();
    }
}
