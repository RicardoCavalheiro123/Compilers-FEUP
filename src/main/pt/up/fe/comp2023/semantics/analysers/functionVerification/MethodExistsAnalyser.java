package pt.up.fe.comp2023.semantics.analysers.functionVerification;

import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.semantics.SemanticVisitor;
import pt.up.fe.comp2023.semantics.symbol_table.MethodTable;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodExistsAnalyser extends SemanticVisitor {
    private List<Report> reportsMethodExists;

    public MethodExistsAnalyser() {
        this.reportsMethodExists = new ArrayList<>();
    }

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("MethodCall", this::visitMethodExists);
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsMethodExists; }

    public Integer visitMethodExists(JmmNode node, SymbolTable symbolTable) {

        if(Objects.equals(node.getKind(), "MethodCall")) {
            var a = getJmmNodeType(node.getJmmChild(0), symbolTable);

            if(Objects.equals(a.getName(), symbolTable.getClassName()) && (!Objects.equals(symbolTable.getSuper(), ""))) {
                return 0;
            }

            if(Objects.equals(a, new Type("invalid", false))) {
                if(imported(node.getJmmChild(0).get("id"), symbolTable)) return 0;
            }
            else {
                if(imported(a.getName(), symbolTable)) return 0;
            }
        }


        //Find method, null if not found
        MethodTable method = symbolTable.findMethod(node.get("method"));

        //Check if method is implemented and has super class
        if(method == null && symbolTable.getSuper() == "") {
            reportsMethodExists.add(
                new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("lineStart")),
                    Integer.parseInt(node.get("colStart")),
                    "Method not implemented in class."
            ));

            return 0;
        }

        return 0;
    }
}
