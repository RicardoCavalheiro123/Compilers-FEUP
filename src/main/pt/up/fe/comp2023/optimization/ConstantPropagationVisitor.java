package pt.up.fe.comp2023.optimization;

import pt.up.fe.comp.jmm.ast.*;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.HashMap;
import java.util.function.BiFunction;

public class ConstantPropagationVisitor extends PreorderJmmVisitor<Boolean, Boolean> {

    private HashMap<String, String> variables;

    @Override
    protected void buildVisitor() {
        this.variables = new HashMap<>();

        setDefaultVisit(this::defaultVisit);

        addVisit("Assign", this::assignVisit);
        addVisit("Identifier", this::identifierVisit);
    }

    public Boolean defaultVisit(JmmNode node, Boolean bool){
        return false; // no changes
    }

    public Boolean assignVisit(JmmNode node, Boolean bool) {
        var value = node.getJmmChild(0);
        var id = node.get("id");

        if(value.getKind().equals("Integer")) {
            variables.put(id, value.get("value"));
        }
        else if(value.getKind().equals("Boolean")) {
            variables.put(id, value.get("value"));
        }
        else if(value.getKind().equals("BinaryOp")) {
            boolean changed = false;

            var op1 = value.getJmmChild(0);
            var op2 = value.getJmmChild(1);

            JmmNodeImpl aux_node, aux_node2; // need to update node child 0

            switch(variables.get(op1.get("id"))) {
                case "false", "true":
                    aux_node = new JmmNodeImpl("Boolean");
                default:
                    aux_node = new JmmNodeImpl("Integer");
            }

            if(op1.getKind().equals("Identifier") && variables.containsKey(op1.get("id"))) {

                aux_node.put("value", variables.get(op1.get("id")));
                aux_node.put("colStart", op1.get("colStart"));
                aux_node.put("lineStart", op1.get("lineStart"));
                aux_node.put("colEnd", op1.get("colEnd"));
                aux_node.put("lineEnd", op1.get("lineEnd"));

                value.setChild(aux_node, 0);

                changed |= true;
            }

            switch(variables.get(op2.get("id"))) {
                case "false", "true":
                    aux_node2 = new JmmNodeImpl("Boolean");
                default:
                    aux_node2 = new JmmNodeImpl("Integer");
            }

            if(op2.getKind().equals("Identifier") && variables.containsKey(op2.get("id"))) {

                aux_node2.put("value", variables.get(op2.get("id")));
                aux_node2.put("colStart", op2.get("colStart"));
                aux_node2.put("lineStart", op2.get("lineStart"));
                aux_node2.put("colEnd", op2.get("colEnd"));
                aux_node2.put("lineEnd", op2.get("lineEnd"));

                value.setChild(aux_node2, 1);

                changed |= true;
            }

            return changed;
        }

        return false;
    }

    public Boolean identifierVisit(JmmNode node, Boolean bool) {
        //var identifier = node.get("name");

        return false;
    }

}
