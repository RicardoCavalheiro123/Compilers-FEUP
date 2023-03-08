package pt.up.fe.comp2023.symbol.table;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.lang.reflect.Parameter;
import java.util.*;

public class MethodTable {
    List<Symbol> parameters = new ArrayList<>();
    Type returnType;
    HashMap<Symbol, Boolean> variables = new HashMap<>();

    public MethodTable(Type returnType, List<Symbol> parameters, HashMap<Symbol, Boolean> variables) {
        this.returnType = returnType;
        this.parameters = parameters;
        this.variables = variables;
    }

    public MethodTable(JmmNode node) {
        if(node.get("name").equals("main")) {

            returnType = new Type("void", false);
            parameters.add(new Symbol(new Type(node.getJmmChild(0).get("id"), true), "args"));

        } else {

            List<JmmNode> children = node.getChildren();
            ArrayList<String> vars = (ArrayList<String>) node.getOptionalObject("vars").get();

            JmmNode returnNode = children.get(0);
            returnType = new Type(returnNode.get("id"), returnNode.get("isArray").equals("true"));

            for (int i=1; i<children.size(); i++) {
                JmmNode child = children.get(i);
                if (child.getKind().equals("Type")) {
                    Type type = new Type(child.get("id"), child.get("isArray").equals("true"));
                    parameters.add(new Symbol(type, vars.get(i - 1)));
                }
            }
        }
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public List<Symbol> getLocalVariables() {
        return new ArrayList<>(variables.keySet());
    }
}
