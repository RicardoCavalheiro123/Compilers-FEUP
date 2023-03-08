package pt.up.fe.comp2023;

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
    }

    private Boolean defaultVisit(JmmNode jmmNode, SymbolTable st) { return null; }

    private Boolean dealWithMethod(JmmNode jmmNode, SymbolTable st) {
        st.methods.put(jmmNode.get("name"), new MethodTable(jmmNode));
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
