package executor;

import executor.ir.Block;
import executor.ir.GlobalBlock;
import executor.ir.Scope;
import executor.ir.expressions.*;
import executor.ir.instructions.*;

public interface Executor extends Visitor {

    void execute(GlobalBlock globalBlock, Scope scope);
    void execute(Block block, Scope scope);
    void execute(AddExpression addExpression, Scope scope);
    void execute(AndExpression andExpression, Scope scope);
    void execute(AsExpression asExpression, Scope scope);
    void execute(AssignmentExpression assignmentExpression, Scope scope);
    void execute(BaseExpression baseExpression, Scope scope);
    void execute(CompExpression compExpression, Scope scope);
    void execute(ConstExpression constExpression, Scope scope);
    void execute(DivExpression divExpression, Scope scope);
    void execute(DivIntExpression divIntExpression, Scope scope);
    void execute(FunctionCall functionCall, Scope scope);
    void execute(Identifier identifier, Scope scope);
    void execute(InsideMatchCompExpression insideMatchCompExpression, Scope scope);
    void execute(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope);
    void execute(IsExpression isExpression, Scope scope);
    void execute(LibFunctionCall libFunctionCall, Scope scope);
    void execute(ModExpression modExpression, Scope scope);
    void execute(MulExpression mulExpression, Scope scope);
    void execute(NullCheckExpression nullCheckExpression, Scope scope);
    void execute(OrExpression orExpression, Scope scope);
    void execute(SubExpression subExpression, Scope scope);
    void execute(UnaryExpression unaryExpression, Scope scope);
    void execute(BreakInstruction breakInstruction, Scope scope);
    void execute(ContinueInstruction continueInstruction, Scope scope);
    void execute(IfInstruction ifInstruction, Scope scope);
    void execute(InsideMatchInstruction insideMatchInstruction, Scope scope);
    void execute(InstructionExpression instructionExpression, Scope scope);
    void execute(MatchInstruction matchInstruction, Scope scope);
    void execute(ReturnInstruction returnInstruction, Scope scope);
    void execute(VarDeclaration varDeclaration, Scope scope);
    void execute(WhileInstruction whileInstruction, Scope scope);
}
