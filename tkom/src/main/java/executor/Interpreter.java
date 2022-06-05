package executor;

import executor.ir.Block;
import executor.ir.GlobalBlock;
import executor.ir.Scope;
import executor.ir.expressions.*;
import executor.ir.instructions.*;

import java.util.Stack;

public class Interpreter implements Executor {

    private final Stack<Void> executionStack = new Stack<>();//TODO: change Void

    public Interpreter() {

    }

    public void run() {

    }


    @Override
    public void execute(GlobalBlock globalBlock, Scope scope) {

    }

    @Override
    public void execute(Block block, Scope scope) {

    }

    @Override
    public void execute(AddExpression addExpression, Scope scope) {

    }

    @Override
    public void execute(AndExpression andExpression, Scope scope) {

    }

    @Override
    public void execute(AsExpression asExpression, Scope scope) {

    }

    @Override
    public void execute(AssignmentExpression assignmentExpression, Scope scope) {

    }

    @Override
    public void execute(BaseExpression baseExpression, Scope scope) {

    }

    @Override
    public void execute(CompExpression compExpression, Scope scope) {

    }

    @Override
    public void execute(ConstExpression constExpression, Scope scope) {

    }

    @Override
    public void execute(DivExpression divExpression, Scope scope) {

    }

    @Override
    public void execute(DivIntExpression divIntExpression, Scope scope) {

    }

    @Override
    public void execute(FunctionCall functionCall, Scope scope) {

    }

    @Override
    public void execute(Identifier identifier, Scope scope) {

    }

    @Override
    public void execute(InsideMatchCompExpression insideMatchCompExpression, Scope scope) {

    }

    @Override
    public void execute(InsideMatchTypeExpression insideMatchTypeExpression, Scope scope) {

    }

    @Override
    public void execute(IsExpression isExpression, Scope scope) {

    }

    @Override
    public void execute(LibFunctionCall libFunctionCall, Scope scope) {

    }

    @Override
    public void execute(ModExpression modExpression, Scope scope) {

    }

    @Override
    public void execute(MulExpression mulExpression, Scope scope) {

    }

    @Override
    public void execute(NullCheckExpression nullCheckExpression, Scope scope) {

    }

    @Override
    public void execute(OrExpression orExpression, Scope scope) {

    }

    @Override
    public void execute(SubExpression subExpression, Scope scope) {

    }

    @Override
    public void execute(UnaryExpression unaryExpression, Scope scope) {

    }

    @Override
    public void execute(BreakInstruction breakInstruction, Scope scope) {

    }

    @Override
    public void execute(ContinueInstruction continueInstruction, Scope scope) {

    }

    @Override
    public void execute(IfInstruction ifInstruction, Scope scope) {

    }

    @Override
    public void execute(InsideMatchInstruction insideMatchInstruction, Scope scope) {

    }

    @Override
    public void execute(InstructionExpression instructionExpression, Scope scope) {

    }

    @Override
    public void execute(MatchInstruction matchInstruction, Scope scope) {

    }

    @Override
    public void execute(ReturnInstruction returnInstruction, Scope scope) {

    }

    @Override
    public void execute(VarDeclaration varDeclaration, Scope scope) {

    }

    @Override
    public void execute(WhileInstruction whileInstruction, Scope scope) {

    }


}
