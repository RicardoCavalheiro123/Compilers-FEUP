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
import java.util.Objects;

public class OperandsTypeCompatibilityAnalyzer extends SemanticVisitor {
    private List<Report> reportsOperandsType;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("BinaryOp", this::visitOperandsType);
    }

    public OperandsTypeCompatibilityAnalyzer() {
        this.reportsOperandsType = new ArrayList<>();
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsOperandsType; }

    public Integer visitOperandsType(JmmNode node, SymbolTable symbolTable) {

        if(!(Objects.equals(this.getJmmNodeType(node, symbolTable), new Type("int", false))) &&
            !(Objects.equals(this.getJmmNodeType(node, symbolTable), new Type("boolean", false)))) {
            reportsOperandsType.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Operand types are not compatible for Binary expression!"
                ));

            return 0;
        }

        return 0;
    }
}
