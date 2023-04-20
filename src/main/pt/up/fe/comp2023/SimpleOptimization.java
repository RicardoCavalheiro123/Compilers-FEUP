package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp2023.ollir.OllirGenerator;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

public class SimpleOptimization implements JmmOptimization {
    @Override
    public OllirResult toOllir(JmmSemanticsResult jmmSemanticsResult) {
        System.out.println("Ollir stage");

        StringBuilder ollir = new StringBuilder();


        OllirGenerator visitor = new OllirGenerator((SymbolTable) jmmSemanticsResult.getSymbolTable(), ollir);

        JmmNode root = jmmSemanticsResult.getRootNode();

        visitor.visit(root, new StringBuilder(""));


        return new OllirResult(jmmSemanticsResult, visitor.getOllirCode(),jmmSemanticsResult.getReports());
    }
}
