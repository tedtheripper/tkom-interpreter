package executor.ir;

import semcheck.TypeVisitor;
import semcheck.exception.SemCheckException;

public interface Expression {

    Type evaluateType(TypeVisitor visitor, Scope scope) throws SemCheckException;
}
