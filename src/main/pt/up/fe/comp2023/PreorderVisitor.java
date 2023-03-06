package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class PreorderVisitor extends PreorderJmmVisitor<String, String> {
    SymbolTable symbolTable = new SymbolTable();

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("program", this::dealWithProgram);
        addVisit("class", this::dealWithClass);
        addVisit("method", this::dealWithMethod);
    }

    private String defaultVisit(JmmNode jmmNode, String s) {
        return null;
    }

    private String dealWithMethod(JmmNode jmmNode, String string) {
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, string);
        }
        return null;
    }

    private String dealWithClass(JmmNode jmmNode, String string) {
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, string);
        }
        return null;
    }

    private String dealWithProgram(JmmNode jmmNode, String string) {
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, string);
        }
        return null;
    }
}
