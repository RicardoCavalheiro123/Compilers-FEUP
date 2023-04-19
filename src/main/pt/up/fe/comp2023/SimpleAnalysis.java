package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.semantics.SemanticVisitor;
import pt.up.fe.comp2023.semantics.analysers.function_verification.MainAnalyzer;
import pt.up.fe.comp2023.semantics.analysers.function_verification.MethodArgumentsCompatibilityAnalyzer;
import pt.up.fe.comp2023.semantics.analysers.function_verification.MethodExistsAnalyser;
import pt.up.fe.comp2023.semantics.analysers.function_verification.MethodReturnTypeAnalyzer;
import pt.up.fe.comp2023.semantics.ASTVisitor;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;
import pt.up.fe.comp2023.semantics.analysers.type_verification.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleAnalysis implements JmmAnalysis {

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult jmmParserResult) {
        List<Report> reports = new ArrayList<>(jmmParserResult.getReports());

        try {
            JmmNode root = jmmParserResult.getRootNode();

            ASTVisitor visitor = new ASTVisitor();

            visitor.visit(root, null);

            SymbolTable symbolTable = visitor.getSymbolTable();

            reports.addAll(visitor.getReports());

            /*List<SemanticVisitor> semantic_analyzers = Arrays.asList(
                new ArrayAccessAnalyzer(),
                new OperandsTypeCompatibilityAnalyzer(),
                new ExpressionConditionAnalyzer(),
                new AssigneeAssignedCompatibilityAnalyzer(),
                new IdentifierAnalyzer(),
                new MethodExistsAnalyser(),
                new MainAnalyzer(),
                new MethodArgumentsCompatibilityAnalyzer(),
                new MethodReturnTypeAnalyzer()
            );

            System.out.println("Performing semantic analysis...");



            for(SemanticVisitor analyzer: semantic_analyzers) {
                analyzer.visit(root, symbolTable);
                reports.addAll(analyzer.getReports());
            }*/

            return new JmmSemanticsResult(jmmParserResult, symbolTable, reports);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
