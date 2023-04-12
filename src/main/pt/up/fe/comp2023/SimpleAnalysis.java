package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.analysers.typeVerification.ArrayAccessAnalyzer;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleAnalysis implements JmmAnalysis {
    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult jmmParserResult) {
        try {
            JmmNode root = jmmParserResult.getRootNode();

            PreorderVisitor visitor = new PreorderVisitor();

            visitor.visit(root, null);

            SymbolTable symbolTable = visitor.getSymbolTable();
            List<Report> reports = new ArrayList<>(visitor.getReports());

            List<PreorderVisitor> semantic_analyzers = Arrays.asList(
               new ArrayAccessAnalyzer()
            );

            System.out.println("Performing semantic analysis...");

            for(var analyzer: semantic_analyzers) {
                analyzer.visit(root, symbolTable);
                reports.addAll(analyzer.getReports());
            }

            return new JmmSemanticsResult(root, symbolTable, reports, jmmParserResult.getConfig());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
