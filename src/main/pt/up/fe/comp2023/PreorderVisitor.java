package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PreorderVisitor extends PreorderJmmVisitor<Object, Object> {
    SymbolTable st = new SymbolTable();
    private List<Report> reports;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        this.reports = new ArrayList<>();
        addVisit("ImportDeclaration", this::defaultImport);
        addVisit("ClassDeclaration", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("VarDeclaration", this::dealWithVarDeclaration);
    }

    public List<Report> getReports() {
        return reports;
    }

    public void addReport(Report report) {
        this.reports.add(report);
    }

    protected Type getIdType(JmmNode node, SymbolTable symbolTable) {
        MethodTable parent_method = null;
        Boolean is_main = false;

        var ancestor_node = node.getAncestor("Parenthesis");

        List<Symbol> symbols = new ArrayList<>();

        //If parent node is main or not
        if(ancestor_node.isPresent()) {
            var method_head = ancestor_node.get().getJmmChild(0);
            parent_method = symbolTable.findMethod(method_head.get("name"));
        }
        else if (node.getAncestor("MainMethod").isPresent()) {
            parent_method = symbolTable.findMethod("main");
            is_main = true;
        }

        if(parent_method != null) {
            symbols.addAll(parent_method.getLocalVariables());
            symbols.addAll(parent_method.getParameters());
        }
        if(!is_main) {
            symbols.addAll(symbolTable.getFields());
        }

        //Return type
        Type rtrn = new Type("invalid", false);

        for(var symbol: symbols) {
            if(Objects.equals(symbol.getName(), node.get("name"))) {
                rtrn = symbol.getType();
                break;
            }
        }

        if(node.get("name").equals(symbolTable.getClassName())) {
            rtrn = new Type(symbolTable.getClassName(), false);
        }
        if(node.get("name").equals(symbolTable.getSuper())) {
            rtrn = new Type(symbolTable.getSuper(), false);
        }

        return rtrn;
    }

    protected String expectedTypeForOp(String op) {
        switch(op) {
            case "&&":
                break;
            default:
                return "invalid";
        }

        return "invalid";
    }

    protected Type typeOfOp(String op, Type left_type, Type right_type) {
        Type rtrn = null;

        boolean cond1 = left_type.isArray() && right_type.isArray();
        boolean cond2 = Objects.equals(left_type.getName(), "ignore");

        return rtrn;
    }


    //Have expression type returned based on elements of expression
    protected Type evaluateExpressionType(JmmNode node, SymbolTable symbolTable) {
        Type rtrn = null;

        String operation = node.get("op");

        JmmNode left = node.getJmmChild(0);
        Type leftType = this.getJmmNodeType(left, symbolTable);

        JmmNode right = node.getJmmChild(1);
        Type rightType = this.getJmmNodeType(right, symbolTable);

        if(Objects.equals(leftType.getName(), "invalid") || Objects.equals(rightType.getName(), "invalid")) {
            return new Type("invalid", false);
        }


        return rtrn;
    }

    public Boolean literal(String str, SymbolTable symbolTable) {
        return (str.equals("int") || str.equals("boolean") || str.equals("array"));
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

    //Check if Types are compatible
    public Boolean typeCompatibility(Type t1, Type t2, SymbolTable symbolTable) {

        Type type_super_class = new Type(symbolTable.getSuper(), false);
        Type type_class = new Type(symbolTable.getClassName(), false);

        if(t1.equals(t2)) {
            return true;
        }
        else if(t1.equals(new Type("ignore", false)) || t2.equals(new Type("ignore", false))) {
            return true;
        }
        else if(imported(t1.getName(), symbolTable)) {
            if(imported(t2.getName(), symbolTable)) {
                return true;
            }
        }
        else if(t1.equals(type_super_class)) {
            if(t2.equals(type_class) || imported(t2.getName(), symbolTable)) {
                return true;
            }
        }

        return false;
    }

    //Get type of node
    public Type getJmmNodeType(JmmNode node, SymbolTable symbolTable) {
        switch(node.getKind()) {
            case "Identifier":
                return this.getIdType(node, symbolTable);
            case "BinaryOp", "UnaryOp":
                break;
            case "This":
                break;
            case "Integer", "ArrayLength":
                break;
            case "Boolean":
                break;
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

    private Object defaultVisit(JmmNode jmmNode, Object obj) { return null; }

    private Object dealWithMethod(JmmNode jmmNode, Object obj) {
        st.methods.put(jmmNode.get("name"), new MethodTable(jmmNode));
        return true;
    }

    private Object dealWithVarDeclaration(JmmNode jmmNode, Object obj) {
        if (jmmNode.getJmmParent().getKind().equals("Method")) { return true; }

        JmmNode type = jmmNode.getChildren().get(0);
        st.fields.put(new Symbol(
                new Type(type.get("typeName"), type.get("isArray").equals("true")),
                jmmNode.get("var")),
                false
        );

        return true;
    }

    private Object dealWithClass(JmmNode jmmNode, Object obj) {
        st.className = jmmNode.get("name");
        if(jmmNode.hasAttribute("extend")) st.superName = jmmNode.get("extend");
        return true;
    }

    private Object defaultImport(JmmNode jmmNode, Object obj) {
        st.imports.add(jmmNode.get("library"));
        return true;
    }

    public SymbolTable getSymbolTable() {
        return st;
    }
}
