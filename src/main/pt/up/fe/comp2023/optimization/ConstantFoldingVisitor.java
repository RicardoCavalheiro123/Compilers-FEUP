package pt.up.fe.comp2023.optimization;

import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.HashMap;
import java.util.Objects;

public class ConstantFoldingVisitor extends PreorderJmmVisitor<Boolean, Boolean> {

    public HashMap<String, String> variables;

    @Override
    protected void buildVisitor() {
        this.variables = new HashMap<>();

        setDefaultVisit(this::defaultVisit);

        addVisit("Assign", this::assignVisit);
    }

    public Boolean defaultVisit(JmmNode node, Boolean bool){
        return false; // no changes
    }

    public Boolean assignVisit(JmmNode node, Boolean bool) {

        if(node.getJmmChild(0).getKind().equals("BinaryOp")) {

            var n = node.getJmmChild(0);

            if(n.getJmmChild(0).getKind().equals("Identifier") || n.getJmmChild(1).getKind().equals("Identifier")) return false;

            var v1 = n.getJmmChild(0).get("value");
            var v2 = n.getJmmChild(1).get("value");

            String value = null;

            switch(n.get("op")) {
                case "+":
                    value = Integer.toString(Integer.parseInt(v1) + Integer.parseInt(v2));
                    break;
                case "-":
                    value = Integer.toString(Integer.parseInt(v1) - Integer.parseInt(v2));
                    break;
                case "*":
                    value = Integer.toString(Integer.parseInt(v1) * Integer.parseInt(v2));
                    break;
                case "/":
                    value = Integer.toString(Integer.parseInt(v1) / Integer.parseInt(v2));
                    break;
                case "==":
                    if(v1 == v2) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                    break;
                case "!=":
                    if(v1 != v2) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                    break;
                case "<":
                    if(Integer.parseInt(v1) < Integer.parseInt(v2)) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                    break;
                case ">":
                    if(Integer.parseInt(v1) > Integer.parseInt(v2)) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                    break;
                case "<=":
                    if(Integer.parseInt(v1) <= Integer.parseInt(v2)) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                    break;
                case ">=":
                    if(Integer.parseInt(v1) >= Integer.parseInt(v2)) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                    break;
                case "&&":
                    if(Objects.equals(v1, "false") || Objects.equals(v2, "false")) {
                        value = "false";
                    }
                    else {
                        value = "true";
                    }
                    break;
                case "||":
                    if(Objects.equals(v1, "true") || Objects.equals(v2, "true")) {
                        value = "true";
                    }
                    else {
                        value = "false";
                    }
                    break;
            }

            JmmNodeImpl aux_node;

            if(Objects.equals(value, "true") || Objects.equals(value, "false")) {
                aux_node = new JmmNodeImpl("Boolean");
            }
            else {
                aux_node = new JmmNodeImpl("Integer");
            }

            aux_node.put("value", value);
            aux_node.put("colStart", n.get("colStart"));
            aux_node.put("lineStart", n.get("lineStart"));
            aux_node.put("colEnd", n.get("colEnd"));
            aux_node.put("lineEnd", n.get("lineEnd"));

            node.setChild(aux_node, 0);

            variables.put(node.get("id"), value);

            return true;
        }

        return false;
    }



}
