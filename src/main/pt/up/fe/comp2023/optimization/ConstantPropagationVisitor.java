package pt.up.fe.comp2023.optimization;

import pt.up.fe.comp.jmm.ast.*;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;

public class ConstantPropagationVisitor extends PreorderJmmVisitor<Boolean, Boolean> {

    public HashMap<String, String> variables;
    public boolean changes = false;

    @Override
    protected void buildVisitor() {
        this.variables = new HashMap<>();

        setDefaultVisit(this::defaultVisit);

        addVisit("Assign", this::assignVisit);
        addVisit("Identifier", this::identifierVisit);
    }

    public Boolean defaultVisit(JmmNode node, Boolean bool) {

        for (var child : node.getChildren()) {
            changes = visit(child) || changes;
        }

        return changes;
    }

    public Boolean assignVisit(JmmNode node, Boolean bool) {

        var value = node.getJmmChild(0);
        var id = node.get("id");

        if (node.getJmmParent().getKind().equals("Block")) {
            var var_to_remove = node.get("id");

            if (variables.containsKey(var_to_remove)) {
                variables.remove(node.get("id"));
            }

            return false;
        }

        if (value.getKind().equals("Integer")) {
            variables.put(id, value.get("value"));
        } else if (value.getKind().equals("Boolean")) {
            variables.put(id, value.get("value"));
        } else if (value.getKind().equals("BinaryOp")) {
            boolean changed = false;

            var op1 = value.getJmmChild(0);
            var op2 = value.getJmmChild(1);

            JmmNodeImpl aux_node, aux_node2; // need to update node child 0

            if (Objects.equals(op1.getKind(), "Identifier") && variables.containsKey(op1.get("id"))) {
                switch (variables.get(op1.get("id"))) {
                    case "false", "true":
                        aux_node = new JmmNodeImpl("Boolean");
                    default:
                        aux_node = new JmmNodeImpl("Integer");
                }

                if (op1.getKind().equals("Identifier") && variables.containsKey(op1.get("id"))) {

                    aux_node.put("value", variables.get(op1.get("id")));
                    aux_node.put("colStart", op1.get("colStart"));
                    aux_node.put("lineStart", op1.get("lineStart"));
                    aux_node.put("colEnd", op1.get("colEnd"));
                    aux_node.put("lineEnd", op1.get("lineEnd"));

                    value.setChild(aux_node, 0);

                    changed = true;
                }
            }

            if (Objects.equals(op2.getKind(), "Identifier") && variables.containsKey(op2.get("id"))) {
                switch (variables.get(op2.get("id"))) {
                    case "false", "true":
                        aux_node2 = new JmmNodeImpl("Boolean");
                    default:
                        aux_node2 = new JmmNodeImpl("Integer");
                }

                if (op2.getKind().equals("Identifier") && variables.containsKey(op2.get("id"))) {

                    aux_node2.put("value", variables.get(op2.get("id")));
                    aux_node2.put("colStart", op2.get("colStart"));
                    aux_node2.put("lineStart", op2.get("lineStart"));
                    aux_node2.put("colEnd", op2.get("colEnd"));
                    aux_node2.put("lineEnd", op2.get("lineEnd"));

                    value.setChild(aux_node2, 1);

                    changed = true;
                }
            }

            return changed;
        }
        else if (node.getKind().equals("Assign")) {

            if(!node.getJmmChild(0).getKind().equals("UnaryOp")) {
                var identifier = variables.get(node.getJmmChild(0).get("id"));

                var n = node.getJmmChild(0);

                if (identifier != null) {
                    JmmNodeImpl aux_node;

                    switch (identifier) {
                        case "false", "true":
                            aux_node = new JmmNodeImpl("Boolean");
                        default:
                            aux_node = new JmmNodeImpl("Integer");
                    }

                    aux_node.put("value", identifier);
                    aux_node.put("colStart", n.get("colStart"));
                    aux_node.put("lineStart", n.get("lineStart"));
                    aux_node.put("colEnd", n.get("colEnd"));
                    aux_node.put("lineEnd", n.get("lineEnd"));

                    node.setChild(aux_node, 0);

                    variables.put(node.get("id"), identifier);

                    return true;
                }
            }
            else {
                if(node.getJmmChild(0).getJmmChild(0).getKind().equals("Identifier")) {
                    var identifier2 = variables.get(node.getJmmChild(0).getJmmChild(0).get("id"));
                    var n = node.getJmmChild(0).getJmmChild(0);

                    if(identifier2 != null) {
                        JmmNodeImpl aux_node = new JmmNodeImpl("Boolean");

                        aux_node.put("value", identifier2);
                        aux_node.put("colStart", n.get("colStart"));
                        aux_node.put("lineStart", n.get("lineStart"));
                        aux_node.put("colEnd", n.get("colEnd"));
                        aux_node.put("lineEnd", n.get("lineEnd"));

                        n.getJmmParent().setChild(aux_node, 0);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Boolean identifierVisit(JmmNode node, Boolean bool) {

        var value = variables.get(node.get("id"));

        if (value != null) {
            JmmNodeImpl aux_node;
            JmmNodeImpl parent = (JmmNodeImpl) node.getJmmParent();

            int idx = parent.getChildren().indexOf(node);

            if (value.equals("true") || value.equals("false")) {
                aux_node = new JmmNodeImpl("Boolean");
            } else {
                aux_node = new JmmNodeImpl("Integer");
            }

            aux_node.put("value", value);
            aux_node.put("colStart", node.get("colStart"));
            aux_node.put("lineStart", node.get("lineStart"));
            aux_node.put("colEnd", node.get("colEnd"));
            aux_node.put("lineEnd", node.get("lineEnd"));
            aux_node.setParent(node.getJmmParent());

            node.delete();
            parent.add(aux_node, idx);

            return true;
        }

        return false;
    }
}
