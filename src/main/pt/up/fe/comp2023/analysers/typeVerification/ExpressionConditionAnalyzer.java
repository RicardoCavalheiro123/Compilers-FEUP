package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class ExpressionConditionAnalyzer extends PreorderVisitor {
    public ExpressionConditionAnalyzer() {
        super();
        //addVisit();
    }

    public Integer visit(JmmNode node, SymbolTable symbolTable) {
        if(typeCompatibility(getJmmNodeType(node.getJmmChild(0), symbolTable), new Type("boolean", false), symbolTable)) {
            addReport(new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("col")),
                    "Expressions in conditions must return a boolean!"
            ));
        }

        return 0;
    }
}
