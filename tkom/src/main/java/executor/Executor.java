package executor;

import executor.exceptions.RuntimeException;
import executor.ir.Block;
import executor.ir.GlobalBlock;
import executor.ir.Scope;
import executor.ir.expressions.*;
import executor.ir.instructions.*;

public interface Executor extends Visitor {

    void execute(GlobalBlock globalBlock, Scope scope) throws RuntimeException;
    void execute(Block block, Scope scope) throws RuntimeException;
    void execute(AddExpression addExpression, Scope scope) throws RuntimeException;
    void execute(AndExpression andExpression, Scope scope) throws RuntimeException;
    void execute(AsExpression asExpression, Scope scope) throws RuntimeException;
    void execute(AssignmentExpression assignmentExpression, Scope scope) throws RuntimeException;
    void execute(BaseExpression baseExpression, Scope scope) throws RuntimeException;
    void execute(CompExpression compExpression, Scope scope) throws RuntimeException;
    void execute(ConstExpression constExpression, Scope scope) throws RuntimeException;
    void execute(DivExpression divExpression, Scope scope) throws RuntimeException;
    void execute(DivIntExpression divIntExpression, Scope scope) throws RuntimeException;
    void execute(FunctionCall functionCall, Scope scope) throws RuntimeException;
    void execute(Identifier identifier, Scope scope) throws RuntimeException;
    void execute(InsideMatchCompExpression insideMatchCompExpression, Scope scope) throws RuntimeException;
    void execute(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope);
    void execute(IsExpression isExpression, Scope scope) throws RuntimeException;
    void execute(LibFunctionCall libFunctionCall, Scope scope) throws RuntimeException;
    void execute(ModExpression modExpression, Scope scope) throws RuntimeException;
    void execute(MulExpression mulExpression, Scope scope) throws RuntimeException;
    void execute(NullCheckExpression nullCheckExpression, Scope scope) throws RuntimeException;
    void execute(OrExpression orExpression, Scope scope) throws RuntimeException;
    void execute(SubExpression subExpression, Scope scope) throws RuntimeException;
    void execute(UnaryExpression unaryExpression, Scope scope) throws RuntimeException;
    void execute(BreakInstruction breakInstruction, Scope scope);
    void execute(ContinueInstruction continueInstruction, Scope scope);
    void execute(IfInstruction ifInstruction, Scope scope) throws RuntimeException;
    void execute(InsideMatchInstruction insideMatchInstruction, Scope scope) throws RuntimeException;
    void execute(InstructionExpression instructionExpression, Scope scope) throws RuntimeException;
    void execute(MatchInstruction matchInstruction, Scope scope) throws RuntimeException;
    void execute(ReturnInstruction returnInstruction, Scope scope) throws RuntimeException;
    void execute(VarDeclaration varDeclaration, Scope scope) throws RuntimeException;
    void execute(WhileInstruction whileInstruction, Scope scope) throws RuntimeException;
}
