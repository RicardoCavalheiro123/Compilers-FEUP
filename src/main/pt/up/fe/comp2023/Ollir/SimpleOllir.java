package pt.up.fe.comp2023.Ollir;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class SimpleOllir implements JmmOptimization {
    @Override
    public OllirResult toOllir(JmmSemanticsResult jmmSemanticsResult) {
        System.out.println("Ollir stage");

        StringBuilder ollir = new StringBuilder();

        SymbolTable s = (SymbolTable) jmmSemanticsResult.getSymbolTable();
        OllirGenerator visitor = new OllirGenerator((SymbolTable) jmmSemanticsResult.getSymbolTable(), ollir);

        JmmNode root = jmmSemanticsResult.getRootNode();

        visitor.visit(root, new StringBuilder(""));

        System.out.println(visitor.getOllirCode());

        return new OllirResult(jmmSemanticsResult, visitor.getOllirCode(),jmmSemanticsResult.getReports());
    }
}
