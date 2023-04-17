package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

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

    public Type getIdType(JmmNode node, SymbolTable symbolTable) {
        String id = node.get("id");
        var method = node.getAncestor("Method");
        var method_name = method.get().get("name");

        // ver se esta nos parametros da função
        var m2 = symbolTable.findMethod(method_name);
        var params = m2.getParameters();

        for(var param: params) {
            if(Objects.equals(param.getName(), id)) {
                return param.getType();
            }
        }
        // ver se esta nos parametros da class (fields)
        var fields = symbolTable.getFields();

        for(var field: fields) {
            if(Objects.equals(field.getName(), id)) {
                return field.getType();
            }
        }

        // ver se esta nas variaveis da funcao
        var variables = m2.getLocalVariables();

        for(var variable: variables) {
            if(Objects.equals(variable.getName(), id)) {
                return variable.getType();
            }
        }

        return new Type("invalid", false);
    }

    public Boolean literal(String str, SymbolTable symbolTable) {
        return (str.equals("int") || str.equals("boolean") || str.equals("String"));
    }

    //Check if imported
    public Boolean imported(String str, SymbolTable symbolTable) {
        for(String impt: symbolTable.getImports()) {
            var imports_split = Arrays.asList(impt.trim().split("\\."));

            if(Objects.equals(str, imports_split.get(imports_split.size() - 1))) {
                return true;
            }
        }

        return false;
    }

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

        return false;
    }

    protected Type getExpressionType(JmmNode node, SymbolTable symbolTable) {
        var type1 = node.getJmmChild(0).getKind();
        var type2 = node.getJmmChild(1).getKind();

        if(Objects.equals(type1, type2) && type1 == "int" && type2 == "int") {
            return new Type("int", false);
        }

        return new Type("invalid", false);
    }

    protected Type getUnOpType(JmmNode node, SymbolTable symbolTable) {
        var child = this.getJmmNodeType(node.getJmmChild(0), symbolTable);

        if(!(Objects.equals(child, new Type("Boolean", false)))) {
            return new Type("UnaryOp", false);
        }

        return new Type("invalid", false);
    }

    //Get type of node
    public Type getJmmNodeType(JmmNode node, SymbolTable symbolTable) {
        switch(node.getKind()) {
            case "Identifier":
                Type a = this.getIdType(node, symbolTable);
                return a;
            case "BinaryOp":
                Type b = this.getExpressionType(node, symbolTable);
                return b;
            case "This":
                return new Type(symbolTable.getClassName(), false);
            case "Integer", "ArrayLength":
                return new Type("int", false);
            case "Boolean":
                return new Type("Boolean", false);
            case "UnaryOp":
                Type c = this.getUnOpType(node, symbolTable);
                return c;
            case "MethodCall":
                break;
            case "NewIntArray":
                break;
            case "NewObject":
                break;
            default:
                return new Type("invalid", false);
        }

        return new Type("invalid", false);
    }
}
