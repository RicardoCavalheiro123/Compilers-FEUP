package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.*;

import java.util.*;

public class LiveNode {
    public Instruction instruction;

    //Variables
    public ArrayList<Element> in = new ArrayList<>();
    public ArrayList<Element> out = new ArrayList<>();
    public ArrayList<Element> def = new ArrayList<>();
    public ArrayList<Element> use = new ArrayList<>();

    public LiveNode() {
    }

    public Map<Element, Element> calculate(ArrayList<Element> set) {
        var map = new HashMap<Element, Element>();

        for(int n1 = 0; n1 < set.size() - 1; n1++) {
            for(int n2 = n1 + 1; n2 < set.size(); n2++) {
                map.put(set.get(n1), set.get(n2));
            }
        }

        return map;
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
                    use.add(((BinaryOpInstruction) instruct).getRightOperand());
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
                if (((CallInstruction) instruct).getInvocationType() == CallType.arraylength ||
                    ((CallInstruction) instruct).getInvocationType() == CallType.invokevirtual ||
                    ((CallInstruction) instruct).getInvocationType() == CallType.invokespecial )
                {
                    use.add(((CallInstruction) instruct).getFirstArg());
                }

                var operands_list = ((CallInstruction) instruct).getListOfOperands();

                if(operands_list != null) {
                    for(var op : operands_list) {
                        use.add(op);
                    }
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
