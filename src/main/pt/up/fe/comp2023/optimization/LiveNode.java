package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.*;

import java.util.HashSet;
import java.util.Set;

public class LiveNode {
    public Instruction instruction;

    //Variables
    public Set<Element> in = new HashSet<>();
    public Set<Element> out = new HashSet<>();
    public Set<Element> def = new HashSet<org.specs.comp.ollir.Element>();
    public Set<Element> use = new HashSet<>();

    public LiveNode() {
    }

    public void nodeAnalysis(Instruction instruct) {
        switch(instruct.getInstType()) {

            case ASSIGN:
                def.add(((AssignInstruction) instruct).getDest());
                nodeAnalysis(((AssignInstruction) instruct).getRhs());
                break;

            case NOPER:
                if(!((SingleOpInstruction) instruct).getSingleOperand().isLiteral()) {
                    use.add(((SingleOpInstruction) instruct).getSingleOperand());
                }
                break;

            case BINARYOPER:

                if(!((BinaryOpInstruction) instruct).getLeftOperand().isLiteral()) {
                    use.add(((BinaryOpInstruction) instruct).getLeftOperand());
                }

                if(!((BinaryOpInstruction) instruct).getRightOperand().isLiteral()) {
                    use.add(((BinaryOpInstruction) instruct).getLeftOperand());
                }

                break;

            case UNARYOPER:

                if(!(((UnaryOpInstruction) instruct).getOperand().isLiteral())) {
                    use.add(((UnaryOpInstruction) instruct).getOperand());
                }

                break;

            case PUTFIELD:

                if(!((PutFieldInstruction) instruct).getSecondOperand().isLiteral()) {
                    def.add(((PutFieldInstruction) instruct).getSecondOperand());
                }

                if(!((PutFieldInstruction) instruct).getThirdOperand().isLiteral()) {
                    use.add(((PutFieldInstruction) instruct).getThirdOperand());
                }

                break;

            case GETFIELD:

                if(!((GetFieldInstruction) instruct).getFirstOperand().isLiteral()) {
                    use.add(((GetFieldInstruction) instruct).getFirstOperand());
                }

                if(!((GetFieldInstruction) instruct).getSecondOperand().isLiteral()) {
                    use.add(((GetFieldInstruction) instruct).getSecondOperand());
                }

                break;

            case BRANCH:
                var aux = ((CondBranchInstruction) instruct);

                if(aux instanceof SingleOpCondInstruction) {
                    var aux2 = ((SingleOpCondInstruction) aux);

                    if(!aux2.getCondition().getSingleOperand().isLiteral()) {
                        nodeAnalysis(aux2.getCondition());
                    }
                }

                break;

            case CALL:
                if (((CallInstruction) instruct).getInvocationType() == CallType.invokevirtual ||
                        ((CallInstruction) instruct).getInvocationType() == CallType.invokespecial ||
                        ((CallInstruction) instruct).getInvocationType() == CallType.arraylength
                ) {

                }
                break;

            case RETURN:
                if(((ReturnInstruction) instruct).getOperand() == null) break;

                if(!((ReturnInstruction) instruct).getOperand().isLiteral()) {
                    use.add(((ReturnInstruction) instruct).getOperand());
                }

                break;

            default:
                break;

        }
    }


}
