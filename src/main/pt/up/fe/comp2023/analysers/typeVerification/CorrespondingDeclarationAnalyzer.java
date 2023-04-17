package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.SemanticVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CorrespondingDeclarationAnalyzer extends SemanticVisitor {
    private List<Report> reportsCorrespondingDeclaration;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("VarDeclaration", this::visitCorrespondingDeclaration);
    }

    public CorrespondingDeclarationAnalyzer() { this.reportsCorrespondingDeclaration = new ArrayList<>(); }

    private Integer defaultVisit(JmmNode node, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsCorrespondingDeclaration; }

    public Integer visitCorrespondingDeclaration(JmmNode node, SymbolTable symbolTable) {
        /*var n = 10;
        var type = node.get("typeName");

        if(type == null) {
            return 0;
        }
        else if(!(imported(type, symbolTable) ||
                literal(type, symbolTable) ||
                (Objects.equals(type, symbolTable.getSuper()) && (symbolTable.getSuper() != null)) ||
                Objects.equals(type, symbolTable.getClassName()))) {

            reportsCorrespondingDeclaration.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Declaration type must be of known type!"
                ));
        }
           */


        return 0;
    }
}
