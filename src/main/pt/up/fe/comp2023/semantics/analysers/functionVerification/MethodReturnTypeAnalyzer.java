package pt.up.fe.comp2023.semantics.analysers.functionVerification;

import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.semantics.SemanticVisitor;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodReturnTypeAnalyzer extends SemanticVisitor {
    private List<Report> reportsMethodReturn;

    public MethodReturnTypeAnalyzer() {
        this.reportsMethodReturn = new ArrayList<>();
    }

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("Ret", this::visitMethodReturnType);
    }

    private Integer defaultVisit(JmmNode node, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsMethodReturn; }

    public Integer visitMethodReturnType(JmmNode node, SymbolTable symbolTable) {
        var declared_return_type = getJmmNodeType(node.getJmmParent().getJmmChild(0), symbolTable);
        var return_type = getJmmNodeType(node.getJmmChild(0), symbolTable);

        if(node.getJmmChild(0).getKind().equals("MethodCall")) {
            var a = symbolTable.findMethod(node.getJmmChild(0).get("method"));

            if (a == null) {
                return 0;
            }
            else {
                var type = a.getReturnType();

                if(!Objects.equals(type, declared_return_type)) {
                    reportsMethodReturn.add(
                        new Report(
                            ReportType.ERROR, Stage.SEMANTIC,
                            Integer.parseInt(node.get("lineStart")),
                            Integer.parseInt(node.get("colStart")),
                            "Return type does not match method declaration!"
                        ));
                }

                return 0;
            }
        }

        if(!Objects.equals(declared_return_type, return_type) ||
            Objects.equals(declared_return_type, new Type("invalid", false)) ||
            Objects.equals(return_type, new Type("invalid", false))) {
            reportsMethodReturn.add(
                new Report(
                    ReportType.ERROR, Stage.SEMANTIC,
                    Integer.parseInt(node.get("lineStart")),
                    Integer.parseInt(node.get("colStart")),
                    "Return type does not match method declaration!"
                ));
        }

        return 0;
    }
}
