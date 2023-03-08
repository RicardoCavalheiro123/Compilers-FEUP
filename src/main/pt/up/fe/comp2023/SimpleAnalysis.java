package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleAnalysis implements JmmAnalysis {
    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult jmmParserResult) {
        JmmNode root = jmmParserResult.getRootNode();

        PreorderVisitor visitor = new PreorderVisitor();

        SymbolTable symbolTable = new SymbolTable();
        visitor.visit(root, symbolTable);

        List<Report> reports = new ArrayList<Report>();

        return new JmmSemanticsResult(root, symbolTable, reports, jmmParserResult.getConfig());
    }
}
