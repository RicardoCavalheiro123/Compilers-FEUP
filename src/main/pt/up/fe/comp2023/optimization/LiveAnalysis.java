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
                Set<Element> in = new HashSet<>(instruction.in);
                Set<Element> out = new HashSet<>(instruction.out);

                Set<Element> newIn = new HashSet<>();
                Set<Element> newOut = new HashSet<>();
                Set<Element> set = new HashSet<>();
                set.addAll(instruction.out);
                set.removeAll(instruction.def);
                newIn.addAll(set);


                for(var successor : instruction.instruction.getSuccessors()) {
                    var liveNode = getLiveNode(instructions, successor);
                    newOut.addAll(liveNode.in);
                }


                if(!newIn.equals(in) || !newOut.equals(out)) {
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

            if(liveNode.instruction.equals(node.getPredecessors().get(0))) {
                return liveNode;
            }
        }
        return null;
    }


}
