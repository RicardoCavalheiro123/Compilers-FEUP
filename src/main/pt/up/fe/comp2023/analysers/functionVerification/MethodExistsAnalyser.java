package pt.up.fe.comp2023.analysers.functionVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class MethodExistsAnalyser extends PreorderVisitor {
    public MethodExistsAnalyser() {
        super();
        //addVisit("This", this::visitThis);
    }

    public Integer visit(JmmNode node, SymbolTable symbolTable) {

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
