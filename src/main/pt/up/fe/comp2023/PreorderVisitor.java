package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class PreorderVisitor extends PreorderJmmVisitor<SymbolTable, Boolean> {
    SymbolTable symbolTable = new SymbolTable();

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("ImportDeclaration", this::defaultImport);
        addVisit("ClassDeclaration", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("VarDeclaration", this::dealWithVarDeclaration);
    }

    private Boolean defaultVisit(JmmNode jmmNode, SymbolTable st) { return null; }

    private Boolean dealWithMethod(JmmNode jmmNode, SymbolTable st) {
        st.methods.put(jmmNode.get("name"), new MethodTable(jmmNode));
        return true;
    }

    private Boolean dealWithVarDeclaration(JmmNode jmmNode, SymbolTable st) {
        if (jmmNode.getJmmParent().getKind().equals("MethodDeclaration")) { return true; }

        JmmNode type = jmmNode.getChildren().get(0);
        st.fields.put(new Symbol(
                new Type(type.get("id"), type.get("isArray").equals("true")),
                jmmNode.get("var")),
                false
        );

        return true;
    }

    private Boolean dealWithClass(JmmNode jmmNode, SymbolTable st) {
        st.className = jmmNode.get("name");
        if(jmmNode.hasAttribute("extend")) st.superName = jmmNode.get("extend");
        return true;
    }

    private Boolean defaultImport(JmmNode jmmNode, SymbolTable st) {
        st.imports.add(jmmNode.get("library"));
        return true;
    }
}
