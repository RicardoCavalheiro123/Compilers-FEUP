package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.*;

import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import java.util.*;

class Graph {
    private Map<String, List<String>> adjacencyMap;
    private Map<String, Integer> colors;
    private Map<String, Descriptor> varTable;
    private int starting_color;

    public Graph(Method method) {
        adjacencyMap = new HashMap<>();
        colors = new HashMap<>();
        varTable = new HashMap<>();
        starting_color = method.isStaticMethod() ? 0 : 1;
    }

    public void setVarTable(Map<String, Descriptor> varTable) {
        this.varTable = varTable;
    }

    public void addNode(String node) {
        if(varTable.get(node).getVarType().getTypeOfElement().equals(ElementType.THIS) ||
           varTable.get(node).getScope().equals(VarScope.PARAMETER)) {

            starting_color++;

            return;
        }

        adjacencyMap.put(node, new ArrayList<>());
        colors.put(node, -1);
    }

    public void addEdge(String source, String destination) {
        if (!adjacencyMap.containsKey(source)) {
            addNode(source);
        }
        if (!adjacencyMap.containsKey(destination)) {
            addNode(destination);
        }

        adjacencyMap.get(source).add(destination);
        adjacencyMap.get(destination).add(source);
    }

    public List<String> getNeighbors(String node) {
        if (!adjacencyMap.containsKey(node)) {
            throw new IllegalArgumentException("Node does not exist in the graph.");
        }
        return adjacencyMap.get(node);
    }

    public int getVarColor(String var) {
        return colors.get(var);
    }

    public Integer graph_coloring() {
        var minimum_registers_number = 1;
        var stack = new Stack<String>();

        Map<String, List<String>> original_neighbours = new HashMap<>(this.adjacencyMap);

        while(original_neighbours.size() > 0) {
            boolean aux = false;

            for(var node: original_neighbours.keySet()) {
                if(original_neighbours.get(node).size() < minimum_registers_number) {
                    stack.push(node);
                    original_neighbours.remove(node);
                    this.remove_neighbour(node, original_neighbours);
                    aux = true;
                }
            }

            if(aux == false) {
                minimum_registers_number += 1;
            }
        }

        while(!stack.isEmpty()) {
            var front = stack.pop();
            List<Integer> neigh_colors = new ArrayList<>();
            var color = this.starting_color;

            for(var neighbour: adjacencyMap.get(front)) {
                neigh_colors.add(colors.get(neighbour));
            }

            while(neigh_colors.contains(color)) {
                color++;
            }

            this.colors.put(front, color);
        }

        return minimum_registers_number;
    }

    private void remove_neighbour(String node_to_remove, Map<String, List<String>> map) {
        for (var node : map.keySet()) {
            for (var neighbour : map.get(node)) {
                if (Objects.equals(neighbour, node_to_remove)) {
                    map.get(node).remove(neighbour);
                }
            }
        }
    }
}
