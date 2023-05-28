package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Method;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.*;

public class RegisterAllocation {

    public void allocation(ClassUnit ollirClass, Integer numberRegisters) {

        Map<Method, List<LiveNode>> liveAnalysis = new LiveAnalysis(ollirClass).analysis();

        create_graph(liveAnalysis, numberRegisters);

    }

    public Report create_graph(Map<Method, List<LiveNode>> map, int numRegisters) {

        for(var key: map.keySet()) {
            if(key.isConstructMethod()) continue;

            var interference_graph = new Graph(key);

            interference_graph.setVarTable(key.getVarTable());

            for(var list_element: map.get(key)) {
                var in = list_element.in;
                var def = list_element.def;
                Set<String> union = new HashSet<>(def);
                union.addAll(list_element.out);

                for(var node1: in) {
                    for(var node2: in) {
                        if(!node1.equals(node2)) {
                            interference_graph.addEdge(node1, node2);
                        }
                    }
                }

                if(union.size() == 1) {
                    interference_graph.addNode(union.iterator().next());
                }

                if(in.size() == 1) {
                    interference_graph.addNode(in.iterator().next());
                }

                for(var node1: union) {
                    for(var node2: union) {
                        if(!node1.equals(node2)) {
                            interference_graph.addEdge(node1, node2);
                        }
                    }
                }

            }


            var minimum_register_number = interference_graph.graph_coloring();

            if(numRegisters > 0 && minimum_register_number > numRegisters) {
                return new Report(
                        ReportType.ERROR,
                        Stage.OPTIMIZATION,
                        -1,
                        -1,
                        "Number of registers not possible!"
                );
            }

            for(var variable : key.getVarTable().keySet()) {
                key.getVarTable().get(variable).setVirtualReg(interference_graph.getVarColor(variable));
            }

            var a = 0;
        }

        return null;
    }
}
