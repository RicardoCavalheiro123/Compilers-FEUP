package pt.up.fe.comp2023.optimization;

import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.HashMap;
import java.util.Objects;

public class ConstantFoldingVisitor extends PreorderJmmVisitor<Boolean, Boolean> {

    public boolean changes = false;

    @Override
    protected void buildVisitor() {

        setDefaultVisit(this::defaultVisit);

        addVisit("BinaryOp", this::binaryopVisit);
        addVisit("UnaryOp", this::unaryopVisit);
    }

    public Boolean defaultVisit(JmmNode node, Boolean bool){

        for(var child: node.getChildren()) {
            changes = visit(child) || changes;
        }

        return changes;
    }

    public Boolean unaryopVisit(JmmNode node, Boolean bool) {
        return false;
    }

    public Boolean binaryopVisit(JmmNode node, Boolean bool) {

        if(node.getJmmChild(0).getKind().equals("Identifier") || node.getJmmChild(1).getKind().equals("Identifier") ||
        node.getJmmChild(0).getKind().equals("BinaryOp") || node.getJmmChild(1).getKind().equals("BinaryOp")) return false;

        var v1 = node.getJmmChild(0).get("value");
        var v2 = node.getJmmChild(1).get("value");

        String value = null;

        switch(node.get("op")) {
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
                if(Objects.equals(v1, v2)) {
                    value = "true";
                }
                else {
                    value = "false";
                }
                break;
            case "!=":
                if(!Objects.equals(v1, v2)) {
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
        aux_node.put("colStart", node.get("colStart"));
        aux_node.put("lineStart", node.get("lineStart"));
        aux_node.put("colEnd", node.get("colEnd"));
        aux_node.put("lineEnd", node.get("lineEnd"));

        node.getJmmParent().setChild(aux_node, 0);

        return true;
    }
}
