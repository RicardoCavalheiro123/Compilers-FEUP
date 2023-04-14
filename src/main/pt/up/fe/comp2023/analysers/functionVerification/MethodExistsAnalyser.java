package pt.up.fe.comp2023.analysers.functionVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.SemanticVisitor;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class MethodExistsAnalyser extends SemanticVisitor {
    private List<Report> reportsArrayAccess;

    public MethodExistsAnalyser() {
        this.reportsArrayAccess = new ArrayList<>();
    }

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("ArrayAccess", this::visitA);
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsArrayAccess; }

    public Integer visitA(JmmNode node, SymbolTable symbolTable) {

        //Check if parent node has current node
        if(node.getJmmParent().getChildren().size() <= 1) {
            return 0;
        }

        //Find method, null if not found
        MethodTable method = symbolTable.findMethod(node.getJmmParent().getJmmChild(1).get("name"));

        //Check if method is implemented and has super class
        if(method == null && symbolTable.getSuper() == null) {
            addReport(new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("col")),
                    "Method " + node.getJmmParent().getJmmChild(1).get("name") + " not implemented in class."
            ));
        }

        return 0;
    }
}
