package pt.up.fe.comp2023.Ollir;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class OllirGenerator extends AJmmVisitor<StringBuilder, String> {

    private final SymbolTable symbolTable;
    private StringBuilder ollirCode;

    public String getOllirCode() {
        return ollirCode.toString();
    }

    public OllirGenerator(SymbolTable symbolTable, StringBuilder ollirCode) {

        this.symbolTable = symbolTable;
        this.ollirCode = ollirCode;

        this.buildVisitor();
    }

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("Program", this::programVisit);
        addVisit("ClassDeclaration", this::dealWithClass);
        addVisit("ImportDeclaration", this::dealWithImport);

    }

    private String programVisit(JmmNode jmmNode, StringBuilder ollirCode) {
        for(String importString: symbolTable.getImports()) {
            ollirCode.append("import ").append(importString).append(";\n");
        }

        for(JmmNode child: jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        return "";
    }


    private String dealWithImport(JmmNode node, StringBuilder ollirCode) {

        for (String s : symbolTable.getImports()) {
            this.ollirCode.append("import ").append(s).append("\n");
        }
        return null;
    }

    private String dealWithClass(JmmNode node, StringBuilder ollirCode) {

        String x = symbolTable.getClassName();
        ollirCode.append(symbolTable.getClassName()).append(" {\n");

        return null;

    }

    private String defaultVisit(JmmNode jmmNode, StringBuilder ollirCode) {
        return null;
    }



}
