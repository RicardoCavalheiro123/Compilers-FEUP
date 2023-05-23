package pt.up.fe.comp2023.optimization;

import pt.up.fe.comp.jmm.ast.*;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;

public class ConstantPropagationVisitor extends PreorderJmmVisitor<Boolean, Boolean> {

    private HashMap<String, String> variables;

    @Override
    protected void buildVisitor() {
        this.variables = new HashMap<>();

        setDefaultVisit(this::defaultVisit);

        addVisit("Assign", this::assignVisit);
        addVisit("Identifier", this::identifierVisit);
        addVisit("VarDeclaration", this::varVisit);
    }

    public Boolean defaultVisit(JmmNode node, Boolean bool){
        return false; // no changes
    }

    public Boolean assignVisit(JmmNode node, Boolean bool) {
        var value = node.getJmmChild(0);
        var id = node.get("id");

        if(value.getKind().equals("Integer")) {
            variables.put(id, value.get("value"));
            node.delete();

            return true;
        }
        else if(value.getKind().equals("Boolean")) {
            variables.put(id, value.get("value"));
            node.delete();

            return true;
        }
        else if(value.getKind().equals("BinaryOp")) {
            boolean changed = false;

            var op1 = value.getJmmChild(0);
            var op2 = value.getJmmChild(1);

            JmmNodeImpl aux_node, aux_node2; // need to update node child 0

            if(Objects.equals(op1.getKind(), "Identifier")) {
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

            if(Objects.equals(op2.getKind(), "Identifier")) {
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

        return false;
    }

    public Boolean identifierVisit(JmmNode node, Boolean bool) {
        var identifier = node.get("id");

        var value = variables.get(node.get("id"));

        if(value != null) {
            JmmNodeImpl aux_node;
            JmmNodeImpl parent = (JmmNodeImpl) node.getJmmParent();

            int idx = parent.getChildren().indexOf(node);

            if(value.equals("true") || value.equals("false")) {
                aux_node = new JmmNodeImpl("Boolean");
            }
            else {
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

    public Boolean varVisit(JmmNode node, Boolean bool) {

        /*JmmNodeImpl parent = (JmmNodeImpl) node.getJmmParent();
        int idx = parent.getChildren().indexOf(node);*/

        /*var value = variables.get(node.get("var"));

        if(value != null) {
            node.delete();

            return true;
        }*/

        variables.put(node.get("var"), null);
        node.delete();


        return true;
    }

}
