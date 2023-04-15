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
import java.util.Objects;

public class ExpressionConditionAnalyzer extends SemanticVisitor {
    private List<Report> reportsExpressionCondition;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("IfElse", this::visitExpressionCondition);
        addVisit("While", this::visitExpressionCondition);
    }

    public ExpressionConditionAnalyzer() {
        this.reportsExpressionCondition = new ArrayList<>();
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsExpressionCondition; }

    public Integer visitExpressionCondition(JmmNode node, SymbolTable symbolTable) {
        var condition = node.getJmmChild(0);

        if(!Objects.equals(this.getJmmNodeType(condition, symbolTable), new Type("Boolean", false))) {
            reportsExpressionCondition.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Condition in expression not Boolean type!"
            ));
        }
        return 0;
    }
}
