package pt.up.fe.comp2023.semantics.analysers.type_verification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.semantics.SemanticVisitor;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class IdentifierAnalyzer extends SemanticVisitor {
    private List<Report> reportsIdentifier;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("Identifier", this::visitIdentifier);
    }

    public IdentifierAnalyzer() {
        this.reportsIdentifier = new ArrayList<>();
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsIdentifier; }

    public Integer visitIdentifier(JmmNode node, SymbolTable symbolTable) {
        if(!imported(node.get("id"), symbolTable) &&
           getIdType(node, symbolTable).equals(new Type("invalid", false)))
        {
            reportsIdentifier.add(new Report(
                ReportType.ERROR,
                Stage.SEMANTIC,
                Integer.parseInt(node.get("lineStart")),
                Integer.parseInt(node.get("colStart")),
                "Identifier " + node.get("id") + " does not have corresponding declaration!"
            ));

            return 0;
        }

        return 0;
    }
}
