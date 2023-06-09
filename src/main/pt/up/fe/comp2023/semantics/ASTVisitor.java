package pt.up.fe.comp2023.semantics;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.semantics.symbol_table.MethodTable;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ASTVisitor extends PreorderJmmVisitor<Object, Object> {
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

    private Object defaultVisit(JmmNode jmmNode, Object obj) { return null; }

    private Object dealWithMethod(JmmNode jmmNode, Object obj) {
        st.methods.put(jmmNode.get("name"), MethodTable.newInstance(jmmNode));
        return true;
    }

    private Object dealWithVarDeclaration(JmmNode jmmNode, Object obj) {
        if (jmmNode.getJmmParent().getKind().equals("Method") || jmmNode.getJmmParent().getKind().equals("MainMethod")) { return true; }

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
