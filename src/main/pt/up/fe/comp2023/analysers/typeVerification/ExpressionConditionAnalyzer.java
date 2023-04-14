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

public class ExpressionConditionAnalyzer extends SemanticVisitor {
    private List<Report> reportsArrayAccess;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("ArrayAccess", this::visitExpressionCondition);
    }

    public ExpressionConditionAnalyzer() {
        this.reportsArrayAccess = new ArrayList<>();
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsArrayAccess; }

    public Integer visitExpressionCondition(JmmNode node, SymbolTable symbolTable) {


        return 0;
    }
}
