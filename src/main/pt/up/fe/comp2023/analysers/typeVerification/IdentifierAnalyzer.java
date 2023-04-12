package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class IdentifierAnalyzer extends PreorderVisitor {
    public IdentifierAnalyzer() {
        super();
    }

    public Integer visit(JmmNode node, SymbolTable symbolTable) {
        if(!imported(node.get("name"), symbolTable) &&
           getIdType(node, symbolTable).equals(new Type("invalid", false)))
        {
        addReport(new Report(
            ReportType.ERROR,
            Stage.SEMANTIC,
            Integer.parseInt(node.get("line")),
            Integer.parseInt(node.get("col")),
            "Identifier " + node.get("name") + "does not have corresponding declaration!"
        ));
        }

        return 0;
    }
}
