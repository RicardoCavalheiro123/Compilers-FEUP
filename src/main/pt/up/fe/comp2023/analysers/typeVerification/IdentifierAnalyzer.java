package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.SemanticVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class IdentifierAnalyzer extends SemanticVisitor {
    private List<Report> reportsArrayAccess;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("ArrayAccess", this::visitA);
    }

    public IdentifierAnalyzer() {
        this.reportsArrayAccess = new ArrayList<>();
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsArrayAccess; }

    public Integer visitA(JmmNode node, SymbolTable symbolTable) {
        if(!imported(node.get("name"), symbolTable) &&
           getIdType(node, symbolTable).equals(new Type("invalid", false)))
        {
        addReport(new Report(
            ReportType.ERROR,
            Stage.SEMANTIC,
            Integer.parseInt(node.get("line")),
            Integer.parseInt(node.get("col")),
            "Identifier " + node.get("name") + "does not have corresponding declaration!"
        ));
        }

        return 0;
    }
}
