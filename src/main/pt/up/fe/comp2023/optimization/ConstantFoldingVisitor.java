package pt.up.fe.comp2023.optimization;

import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.HashMap;
import java.util.Objects;

public class ConstantFoldingVisitor extends PreorderJmmVisitor<Boolean, Boolean> {

    public boolean changes = false;
    public HashMap<String, String> variables;

    @Override
    protected void buildVisitor() {
        this.variables = new HashMap<>();

        setDefaultVisit(this::defaultVisit);

        addVisit("BinaryOp", this::binaryopVisit);
        addVisit("UnaryOp", this::unaryopVisit);
        addVisit("Parenthesis", this::parenthesisVisit);
    }

    public Boolean defaultVisit(JmmNode node, Boolean bool){

        for(var child: node.getChildren()) {
            changes = visit(child) || changes;
        }

        return changes;
    }

    public Boolean unaryopVisit(JmmNode node, Boolean bool) {

        var n = node.getJmmChild(0);

        String value = null;

        if(n.getKind().equals("Parenthesis")) return false;

        if(n.get("value").equals("true")) {
            value = "false";
        } else {
            value = "true";
        }

        JmmNodeImpl aux_node = new JmmNodeImpl("Boolean");

        aux_node.put("value", value);
        aux_node.put("colStart", n.get("colStart"));
        aux_node.put("lineStart", n.get("lineStart"));
        aux_node.put("colEnd", n.get("colEnd"));
        aux_node.put("lineEnd", n.get("lineEnd"));

        node.replace(aux_node);

        return true;
    }

    public Boolean binaryopVisit(JmmNode node, Boolean bool) {

        if(node.getJmmChild(0).getKind().equals("Identifier") || node.getJmmChild(1).getKind().equals("Identifier") ||
        node.getJmmChild(0).getKind().equals("BinaryOp") || node.getJmmChild(1).getKind().equals("BinaryOp") ||
        node.getJmmChild(0).getKind().equals("UnaryOp") || node.getJmmChild(1).getKind().equals("UnaryOp") ||
        node.getJmmChild(0).getKind().equals("Parenthesis") || node.getJmmChild(1).getKind().equals("Parenthesis")) return false;

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

    public Boolean parenthesisVisit(JmmNode node, Boolean bool) {
        if(node.getJmmChild(0).getKind().equals("Integer") || node.getJmmChild(0).getKind().equals("Boolean")) {
            JmmNodeImpl aux_node = new JmmNodeImpl(node.getJmmChild(0).getKind());

            aux_node.put("value", node.getJmmChild(0).get("value"));
            aux_node.put("colStart", node.get("colStart"));
            aux_node.put("lineStart", node.get("lineStart"));
            aux_node.put("colEnd", node.get("colEnd"));
            aux_node.put("lineEnd", node.get("lineEnd"));

            node.replace(aux_node);

            return true;
        }

        return false;
    }
}
