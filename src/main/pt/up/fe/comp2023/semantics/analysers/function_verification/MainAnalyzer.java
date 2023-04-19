package pt.up.fe.comp2023.semantics.analysers.function_verification;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.semantics.SemanticVisitor;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class MainAnalyzer extends SemanticVisitor {
    private List<Report> reportsMain;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("This", this::visitMainExists);
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public MainAnalyzer() {
        this.reportsMain= new ArrayList<>();
    }

    public List<Report> getReports() { return this.reportsMain; }

    public Integer visitMainExists(JmmNode node, SymbolTable symbolTable) {
        var main = node.getAncestor("MainMethod");

        if(main.isPresent()) {
            reportsMain.add(
                new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("lineStart")),
                    Integer.parseInt(node.get("colStart")),
                    "This cannot be used in main method!"
                ));
        }

        return 0;
    }
}
