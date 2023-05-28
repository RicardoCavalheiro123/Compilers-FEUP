package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.*;

import javax.swing.*;
import java.util.*;

public class LiveAnalysis {

    public ClassUnit ollirClass;

    public LiveAnalysis(ClassUnit ollirClass) {
        this.ollirClass = ollirClass;
    }

    public Map<String, List<LiveNode>> analysis() {
        Map<String, List<LiveNode>> liveAnalysis = new HashMap<>();

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


        }

        return liveAnalysis;
    }

    public void computeInOut(List<LiveNode> instructions){
        boolean changes = true;
        while(changes) {
            for(LiveNode instruction : instructions) {
                var in = new HashSet<>(instruction.in);
                var out = new HashSet<>(instruction.out);

                var newIn = new HashSet<>();
                var newOut = new HashSet<>();
                var set = new HashSet<>();
                set.addAll(instruction.out);
                set.removeAll(instruction.def);
                newIn.addAll(set);



                for(var successor : instruction.instruction.getSuccessors()) {
                    var liveNode = getLiveNode(instructions, successor);
                    newOut.addAll(liveNode.in);
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
