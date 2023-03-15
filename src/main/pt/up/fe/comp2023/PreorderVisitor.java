package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class PreorderVisitor extends PreorderJmmVisitor<Object, Object> {
    SymbolTable st = new SymbolTable();

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("ImportDeclaration", this::defaultImport);
        addVisit("ClassDeclaration", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("VarDeclaration", this::dealWithVarDeclaration);
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
                new Type(type.get("type_"), type.get("isArray").equals("true")),
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
