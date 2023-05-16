package pt.up.fe.comp2023.optimization;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.HashMap;

public class ConstantFoldingVisitor extends PreorderJmmVisitor<Boolean, Boolean> {

    @Override
    protected void buildVisitor() {

        setDefaultVisit(this::defaultVisit);

        addVisit("Assign", this::assignVisit);
    }

    public Boolean defaultVisit(JmmNode node, Boolean bool){
        return false; // no changes
    }

    public Boolean assignVisit(JmmNode node, Boolean bool) {

        if(node.getJmmChild(0).getKind().equals("BinaryOp")) {
            switch(node.getJmmChild(0).get("op")) {
                case "+":
                    break;
                case "-":
                    break;
                case "*":
                    break;
                case "/":
                    break;
                case "==":
                    break;
                case "!=":
                    break;
                case "<":
                    break;
                case ">":
                    break;
                case "<=":
                    break;
                case ">=":
                    break;
                case "&&":
                    break;
                case "||":
                    break;
            }
        }

        return false;
    }



}
