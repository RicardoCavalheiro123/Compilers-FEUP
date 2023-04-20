package pt.up.fe.comp2023.semantics.symbol_table;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;

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

    public static MethodTable newInstance(JmmNode node) {
        List<Symbol> parameters = new ArrayList<>();
        Type returnType = null;
        HashMap<Symbol, Boolean> variables = new HashMap<>();

        List<JmmNode> children = node.getChildren(); // return type, parameters, var declarations and statements
        if(node.get("name").equals("main")) {
            // main method
            returnType = new Type("void", false);
            parameters.add(new Symbol(new Type(node.getJmmChild(0).get("typeName"), true), "args"));
        }
        else {
            JmmNode returnNode = children.get(0);
            returnType = new Type(returnNode.get("typeName"), returnNode.get("isArray").equals("true"));
        }

        for (int i=1; i<children.size(); i++) {
            JmmNode child = children.get(i);
            if (child.getKind().equals("Parameter")) {
                JmmNode typeNode = child.getJmmChild(0);
                Type type = new Type(typeNode.get("typeName"), typeNode.get("isArray").equals("true"));
                parameters.add(new Symbol(type, child.get("var")));

            } else if (child.getKind().equals("VarDeclaration")) {
                Type type = new Type(child.getJmmChild(0).get("typeName"), child.getJmmChild(0).get("isArray").equals("true"));
                variables.put(new Symbol(type, child.get("var")), false);

            }
        }

        return new MethodTable(returnType, parameters, variables);
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
    public HashMap<Symbol, Boolean> getVariables() {
        return variables;
    }
}
