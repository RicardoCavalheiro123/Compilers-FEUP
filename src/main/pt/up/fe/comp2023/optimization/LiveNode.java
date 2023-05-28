package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.*;

import java.util.*;

public class LiveNode {
    public Instruction instruction;


    //Variables
    public ArrayList<String> in = new ArrayList<>();
    public ArrayList<String> out = new ArrayList<>();
    public ArrayList<String> def = new ArrayList<>();
    public ArrayList<String> use = new ArrayList<>();

    public LiveNode() {
    }

/*
    public Map<String, String> calculate(ArrayList<Element> set) {
        var map = new HashMap<String, String>();

        for(int n1 = 0; n1 < set.size() - 1; n1++) {
            for(int n2 = n1 + 1; n2 < set.size(); n2++) {
                map.put(set.get(n1), set.get(n2));
            }
        }

        return map;
    }
*/


    public void nodeAnalysis(Instruction instruct) {
        switch(instruct.getInstType()) {

            case ASSIGN:
                def.add(findName( (Element) ((AssignInstruction) instruct).getDest()));
                nodeAnalysis(((AssignInstruction) instruct).getRhs());
                break;

            case NOPER:
                if(!((SingleOpInstruction) instruct).getSingleOperand().isLiteral()) {
                    use.add(findName((Element) ((SingleOpInstruction) instruct).getSingleOperand()));
                }
                break;

            case BINARYOPER:

                if(!((BinaryOpInstruction) instruct).getLeftOperand().isLiteral()) {
                    use.add(findName( (Element)((BinaryOpInstruction) instruct).getLeftOperand()));
                }

                if(!((BinaryOpInstruction) instruct).getRightOperand().isLiteral()) {
                    use.add(findName( (Element)((BinaryOpInstruction) instruct).getRightOperand()));
                }

                break;

            case UNARYOPER:

                if(!(((UnaryOpInstruction) instruct).getOperand().isLiteral())) {
                    use.add(findName( (Element)((UnaryOpInstruction) instruct).getOperand()));
                }

                break;

            case PUTFIELD:

                if(!((PutFieldInstruction) instruct).getSecondOperand().isLiteral()) {
                    def.add(findName( (Element)((PutFieldInstruction) instruct).getSecondOperand()));
                }

                if(!((PutFieldInstruction) instruct).getThirdOperand().isLiteral()) {
                    use.add(findName( (Element)((PutFieldInstruction) instruct).getThirdOperand()));
                }

                break;

            case GETFIELD:

                if(!((GetFieldInstruction) instruct).getFirstOperand().isLiteral()) {
                    use.add(findName((Element) ((GetFieldInstruction) instruct).getFirstOperand()));
                }

                if(!((GetFieldInstruction) instruct).getSecondOperand().isLiteral()) {
                    use.add(findName((Element) ((GetFieldInstruction) instruct).getSecondOperand()));
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

                var operands_list = ((CallInstruction) instruct).getListOfOperands();

                if(operands_list != null) {
                    for(var op : operands_list) {
                        use.add(findName( (Element) op));
                    }
                }

                break;

            case RETURN:
                if(((ReturnInstruction) instruct).getOperand() == null) break;

                if(!((ReturnInstruction) instruct).getOperand().isLiteral()) {
                    use.add(findName( (Element)((ReturnInstruction) instruct).getOperand()));
                }

                break;

            default:
                break;

        }
    }

    public String findName(Element op) {
        if(op != null) {
            return !op.isLiteral() ? ((Operand) op).getName() : null;
        }
        return null;
    }


}
