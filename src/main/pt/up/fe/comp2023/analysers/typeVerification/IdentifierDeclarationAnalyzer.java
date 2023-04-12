package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class IdentifierDeclarationAnalyzer extends PreorderVisitor {
    public IdentifierDeclarationAnalyzer() {
        super();
    }

    public Integer visit(JmmNode node, SymbolTable symbolTable) {
        if(node.get("varType") == null) {
            return 0;
        }

        if(!(imported(node.get("varType"), symbolTable) ||
             literal(node.get("varType"), symbolTable) ||
             symbolTable.getClassName().equals(node.get("varType")) ||
             (symbolTable.getSuper() != null && symbolTable.getSuper().equals(node.get("varType")))))
        {
            addReport(new Report(
                ReportType.ERROR,
                Stage.SEMANTIC,
                Integer.parseInt(node.get("line")),
                Integer.parseInt(node.get("col")),
                "Identifiers should have corresponding declaration!"
            ));

        }

        return 0;
    }
}
