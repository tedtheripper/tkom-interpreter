package semcheck;

import executor.Visitor;
import executor.ir.Scope;
import executor.ir.Type;
import executor.ir.expressions.*;
import semcheck.exception.SemCheckException;

public interface TypeVisitor extends Visitor {

    Type visit(AddExpression addExpression, Scope scope) throws SemCheckException;

    Type visit(AndExpression andExpression, Scope scope) throws SemCheckException;

    Type visit(AsExpression asExpression, Scope scope) throws SemCheckException;

    Type visit(AssignmentExpression assignmentExpression, Scope scope) throws SemCheckException;

    Type visit(BaseExpression baseExpression, Scope scope) throws SemCheckException;

    Type visit(CompExpression compExpression, Scope scope) throws SemCheckException;

    Type visit(DivExpression divExpression, Scope scope) throws SemCheckException;

    Type visit(DivIntExpression divIntExpression, Scope scope) throws SemCheckException;

    Type visit(Identifier identifier, Scope scope);

    Type visit(InsideMatchCompExpression insideMatchCompExpression, Scope scope) throws SemCheckException;

    Type visit(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope);

    Type visit(IsExpression isExpression, Scope scope);

    Type visit(ModExpression modExpression, Scope scope) throws SemCheckException;

    Type visit(MulExpression mulExpression, Scope scope) throws SemCheckException;

    Type visit(NullCheckExpression nullCheckExpression, Scope scope) throws SemCheckException;

    Type visit(OrExpression orExpression, Scope scope) throws SemCheckException;

    Type visit(SubExpression subExpression, Scope scope) throws SemCheckException;

    Type visit(UnaryExpression unaryExpression, Scope scope) throws SemCheckException;

    Type visit(ConstExpression constExpression, Scope scope);

    Type visit(FunctionCall functionCall, Scope scope) throws SemCheckException;

    Type visit(LibFunctionCall libFunctionCall, Scope scope) throws SemCheckException;
}

