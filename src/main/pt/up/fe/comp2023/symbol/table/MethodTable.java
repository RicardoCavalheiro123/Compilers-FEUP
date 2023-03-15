package pt.up.fe.comp2023.symbol.table;

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

    public MethodTable(JmmNode node) {
        if(node.get("name").equals("main")) {
            // main method
            returnType = new Type("void", false);
            parameters.add(new Symbol(new Type(node.getJmmChild(0).get("type_"), true), "args"));

        } else {

            List<JmmNode> children = node.getChildren(); // return type, parameters, var declarations and statements

            JmmNode returnNode = children.get(0);
            this.returnType = new Type(returnNode.get("type_"), returnNode.get("isArray").equals("true"));

            for (int i=1; i<children.size(); i++) {
                JmmNode child = children.get(i);
                if (child.getKind().equals("Parameter")) {
                    JmmNode typeNode = child.getJmmChild(0);
                    Type type = new Type(typeNode.get("type_"), typeNode.get("isArray").equals("true"));
                    this.parameters.add(new Symbol(type, child.get("var")));

                } else if (child.getKind().equals("VarDeclaration")) {
                    Type type = new Type(child.getJmmChild(0).get("type_"), child.getJmmChild(0).get("isArray").equals("true"));
                    this.variables.put(new Symbol(type, child.get("var")), false);

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
