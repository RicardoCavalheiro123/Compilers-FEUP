package pt.up.fe.comp2023.semantics;

import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.*;

public abstract class SemanticVisitor extends PreorderJmmVisitor<SymbolTable, Integer> {
    private List<Report> reports;

    public SemanticVisitor() {
        this.reports = new ArrayList<>();
    }

    public List<Report> getReports() {
        return this.reports;
    }

    public void addReport(Report report) {
        this.reports.add(report);
    }

    //return node id type
    public Type getIdType(JmmNode node, SymbolTable symbolTable) {
        String id = node.get("id");
        var method = node.getAncestor("Method").isEmpty() ? node.getAncestor("MainMethod").get() : node.getAncestor("Method").get() ;
        var method_name = method.get("name");

        //check existence in function parameters
        var m2 = symbolTable.findMethod(method_name);
        var params = m2.getParameters();

        for(var param: params) {
            if(Objects.equals(param.getName(), id)) {
                return param.getType();
            }
        }

        //check existence in function variables
        var variables = m2.getLocalVariables();

        for(var variable: variables) {
            if(Objects.equals(variable.getName(), id)) {
                return variable.getType();
            }
        }

        //check existence in class parameters (fields)
        var fields = symbolTable.getFields();

        if (node.getAncestor("MainMethod").isEmpty()) {
            for (var field : fields) {
                if (Objects.equals(field.getName(), id)) {
                    return field.getType();
                }
            }
        }

        return new Type("invalid", false);
    }

    //check if imported, true if it is, false otherwise
    public Boolean imported(String str, SymbolTable symbolTable) {
        for(String impt: symbolTable.getImports()) {
            var imports_split = Arrays.asList(impt.trim().split("\\."));
            var string = "";

            for(int i = 1; i < impt.length() - 1; i++) {
                string +=  String.valueOf(imports_split.get(imports_split.size() - 1).charAt(i));
            }

            if(Objects.equals(str, string)) {
                return true;
            }
        }

        return false;
    }

    //check if types t1 and t2 are compatible
    public boolean compatibleType(Type t1, Type t2, SymbolTable symbolTable) {
        Type type_super = new Type(symbolTable.getSuper(), false);
        Type type_class = new Type(symbolTable.getClassName(), false);

        if(Objects.equals(t1, t2)) {
            return true;
        }
        else if(Objects.equals(t1, type_super)) {
            if(Objects.equals(t2, type_class) || this.imported(t2.getName(), symbolTable)) {
                return true;
            }
        }
        else if (this.imported(t1.getName(), symbolTable)) {
            if(this.imported(t2.getName(), symbolTable)) {
                return true;
            }
        }
        else if(Objects.equals(t2, type_super)) {
            if(Objects.equals(t1, type_class) || this.imported(t1.getName(), symbolTable)) {
                return true;
            }
        }
        else if (this.imported(t2.getName(), symbolTable)) {
            if(this.imported(t1.getName(), symbolTable)) {
                return true;
            }
        }

        return false;
    }

    //given a node, return its type
    protected Type getExpressionType(JmmNode node, SymbolTable symbolTable) {
        var type1 = getJmmNodeType(node.getJmmChild(0), symbolTable);
        var type2 = getJmmNodeType(node.getJmmChild(1), symbolTable);

        if(((Objects.equals(type1, type2) && Objects.equals(type1, new Type("int", false))) ||
            (Objects.equals(type1, type2) && Objects.equals(type1, new Type("boolean", false))))
        ){
            if(Objects.equals(node.get("op"), "==") ||
                    Objects.equals(node.get("op"), "!=") ||
                    Objects.equals(node.get("op"), "<") ||
                    Objects.equals(node.get("op"), ">") ||
                    Objects.equals(node.get("op"), "<=") ||
                    Objects.equals(node.get("op"), ">=") ||
                    Objects.equals(node.get("op"), "&&") ||
                    Objects.equals(node.get("op"), "||")
            ) {
                return new Type("boolean", false);
            }

            return new Type("int", false);
        }

        return new Type("invalid", false);
    }

    protected Type getUnOpType(JmmNode node, SymbolTable symbolTable) {
        var child = this.getJmmNodeType(node.getJmmChild(0), symbolTable);

        if((Objects.equals(child, new Type("boolean", false)))) {
            return new Type("boolean", false);
        }

        return new Type("invalid", false);
    }

    //Get type of node
    public Type getJmmNodeType(JmmNode node, SymbolTable symbolTable) {
        switch(node.getKind()) {
            case "Identifier":
                return this.getIdType(node, symbolTable);

            case "BinaryOp":
                return this.getExpressionType(node, symbolTable);

            case "This":
                return new Type(symbolTable.getClassName(), false);

            case "Integer", "ArrayLength", "ArrayAccess":
                return new Type("int", false);

            case "Boolean", "BooleanType":
                return new Type("boolean", false);

            case "UnaryOp":
                return this.getUnOpType(node, symbolTable);

            case "ObjectType":
                return new Type(node.get("typeName"), false);

            case "MethodCall":
                if(symbolTable.findMethod(node.get("method")) == null) {
                    return new Type("inexists", false);
                }

                return symbolTable.findMethod(node.get("method")).getReturnType();

            case "NewIntArray":
                return new Type("int", true);

            case "NewObject":
                return new Type(node.get("id"), false);

            case "IntType":
                if(node.get("isArray").equals("true")) {
                    return new Type("int", true);
                }

                return new Type("int", false);

            case "Parenthesis":
                return this.getJmmNodeType(node.getJmmChild(0), symbolTable);

            case "StringType":
                return new Type("String", false);

            default:
                return new Type("invalid", false);
        }
    }
}
