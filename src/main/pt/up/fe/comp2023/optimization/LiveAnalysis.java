package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            liveAnalysis.put(method, instructions);
        }

        return liveAnalysis;
    }


}
