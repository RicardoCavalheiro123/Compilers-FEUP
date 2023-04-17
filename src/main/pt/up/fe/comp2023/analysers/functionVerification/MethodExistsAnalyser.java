package pt.up.fe.comp2023.analysers.functionVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.SemanticVisitor;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.lang.reflect.Method;
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

        //Check if parent node has current node
        if(node.getJmmParent().getChildren().size() <= 1) {
            return 0;
        }

        //Find method, null if not found
        MethodTable method = symbolTable.findMethod(node.getJmmParent().getJmmChild(1).get("id"));

        //Check if method is implemented and has super class
        if(method == null && symbolTable.getSuper() == null) {
            reportsMethodExists.add(
                new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("lineStart")),
                    Integer.parseInt(node.get("colStart")),
                    "Method " + node.getJmmParent().getJmmChild(1).get("id") + " not implemented in class."
            ));
        }

        if (this.getJmmNodeType(node.getJmmChild(0), symbolTable).getName().equals(symbolTable.getClassName())) {

            JmmNode analyse_node;

            if (Objects.equals(node.getJmmChild(1).getKind(), "NewObject")) {
                analyse_node = node.getJmmChild(1).getJmmChild(0);
            } else {
                analyse_node = node.getJmmChild(1);
            }

            MethodTable method2 = symbolTable.findMethod(analyse_node.get("id"));

            // If method isn't implemented in the class and doesn't have a super class
            if (method2 == null && symbolTable.getSuper() == null) {
                addReport(new Report(
                        ReportType.ERROR, Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Method is not implemented in this class."));
            }
        }


        return 0;
    }
}
