package semcheck;

import executor.ir.Scope;
import executor.ir.Type;
import executor.ir.expressions.*;
import semcheck.exception.SemCheckException;

public interface TypeVisitor {

    Type visit(AddExpression addExpression, Scope scope) throws SemCheckException;

    Type visit(AndExpression andExpression, Scope scope) throws SemCheckException;

    Type visit(AsExpression asExpression, Scope scope);

    Type visit(AssignmentExpression assignmentExpression, Scope scope) throws SemCheckException;

    Type visit(BaseExpression baseExpression, Scope scope) throws SemCheckException;

    Type visit(CompExpression compExpression, Scope scope);

    Type visit(DivExpression divExpression, Scope scope);

    Type visit(DivIntExpression divIntExpression, Scope scope);

    Type visit(Identifier identifier, Scope scope);

    Type visit(InsideMatchCompExpression insideMatchCompExpression, Scope scope);

    Type visit(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope);

    Type visit(IsExpression isExpression, Scope scope);

    Type visit(ModExpression modExpression, Scope scope);

    Type visit(MulExpression mulExpression, Scope scope);

    Type visit(NullCheckExpression nullCheckExpression, Scope scope);

    Type visit(OrExpression orExpression, Scope scope);

    Type visit(SubExpression subExpression, Scope scope);

    Type visit(UnaryExpression unaryExpression, Scope scope) throws SemCheckException;

    Type visit(ConstExpression constExpression, Scope scope);
}

