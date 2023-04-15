package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.SemanticVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AssigneeAssignedCompatibilityAnalyzer extends SemanticVisitor {
    private List<Report> reportsAssigneeAssigned;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("Assign", this::visitAssigneeAssigned);
    }

    public AssigneeAssignedCompatibilityAnalyzer() {
        this.reportsAssigneeAssigned = new ArrayList<>();
    }

    private Integer defaultVisit(JmmNode node, SymbolTable symbolTable) {
        return null;
    }

    public List<Report> getReports() { return this.reportsAssigneeAssigned; }

    public Integer visitAssigneeAssigned(JmmNode node, SymbolTable symbolTable) {
        var assigned = this.getJmmNodeType(node.getJmmChild(0), symbolTable);
        var type = this.getIdType(node, symbolTable);

        if(!this.compatibleType(assigned, type, symbolTable)) {
            reportsAssigneeAssigned.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Assignee is not of assigned type!"
            ));
        }

        return 0;
    }
}
