package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.*;

import javax.swing.*;
import java.util.*;

public class LiveAnalysis {

    public ClassUnit ollirClass;

    public LiveAnalysis(ClassUnit ollirClass) {
        this.ollirClass = ollirClass;
    }

    public Map<Method, List<LiveNode>> analysis() {
        Map<Method, List<LiveNode>> liveAnalysis = new HashMap<>();

        for (var method : ollirClass.getMethods()) {
            //instruction list
            List<LiveNode> instructions = new ArrayList<>();

            for(var instruction : method.getInstructions()) {
                var liveNode = new LiveNode();
                liveNode.instruction = instruction;


                liveNode.nodeAnalysis(liveNode.instruction);

                instructions.add(liveNode);

            }

            computeInOut(instructions);


            liveAnalysis.put(method, instructions);
        }

        return liveAnalysis;
    }

    public void computeInOut(List<LiveNode> instructions){
        boolean changes = true;
        while(changes) {
            for(LiveNode instruction : instructions) {

                ArrayList<String> inBef = new ArrayList<>(instruction.in);
                ArrayList<String> outBef = new ArrayList<>(instruction.out);

                ArrayList<String> resOut = new ArrayList<>();

                for(Node child: instruction.instruction.getSuccessors()) {
                    LiveNode node = getLiveNode(instructions,child);
                    if(node != null) {
                        ArrayList<String> childIn = new ArrayList<>(node.in);
                        resOut.addAll(childIn);
                    }
                }

                instruction.out = resOut;

                ArrayList<String> resIn = new ArrayList<>(instruction.use);
                ArrayList<String> temp = new ArrayList<>(instruction.out);

                temp.removeAll(instruction.def);

                resIn.addAll(temp);

                instruction.in = resIn;

                if(!inBef.equals(instruction.in) || !outBef.equals(instruction.out)) {
                    changes = true;
                }
                else {
                    changes = false;
                }



            }
        }



    }

    public LiveNode getLiveNode(List<LiveNode> instructions, Node node) {
        for(LiveNode liveNode : instructions) {

            if(liveNode.instruction.equals(node)) {
                return liveNode;
            }

        }
        return null;
    }





}
