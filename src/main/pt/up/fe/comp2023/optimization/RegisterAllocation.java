package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Descriptor;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Method;

import java.util.*;

public class RegisterAllocation {

    public void allocation(ClassUnit ollirClass, Integer numberRegisters) {

        Map<Method, List<LiveNode>> liveAnalysis = new LiveAnalysis(ollirClass).analysis();

        create_graph(liveAnalysis);

        var a = 0;

    }

    public void create_graph(Map<Method, List<LiveNode>> map) {

        for(var key: map.keySet()) {
            var interference_graph = new Graph(key);

            interference_graph.setVarTable(key.getVarTable());

            for(var list_element: map.get(key)) {
                var in = list_element.in;
                var def = list_element.def;
                ArrayList<String> union = new ArrayList<>(def);
                union.addAll(list_element.out);

                for(var node1: in) {
                    for(var node2: in) {
                        if(node1 != node2) {
                            interference_graph.addEdge(node1, node2);
                        }
                    }
                }

                for(var node1: union) {
                    for(var node2: union) {
                        if(node1 != node2) {
                            interference_graph.addEdge(node1, node2);
                        }
                    }
                }
            }
        }
    }
}
