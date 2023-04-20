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

public class ArrayAccessAnalyzer extends SemanticVisitor {
    private List<Report> reportsArrayAccess;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("ArrayAccess", this::visitArrayAccess);
        addVisit("ArrayAssign", this::visitArray);
    }

    public ArrayAccessAnalyzer() {
        this.reportsArrayAccess = new ArrayList<>();
    }

    private Integer defaultVisit(JmmNode jmmNode, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsArrayAccess; }

    public Integer visitArrayAccess(JmmNode node, SymbolTable symbolTable) {
        var ancestor = node.getJmmChild(0);
        var index = node.getJmmChild(1);
        var t1 = getJmmNodeType(ancestor, symbolTable);
        var t2 = getJmmNodeType(index, symbolTable);

        //If first is not an array and is trying to be accessed as an array, report error
        if(!(this.getJmmNodeType(ancestor, symbolTable)).isArray()) {
            reportsArrayAccess.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Accessing arrays is only allowed on arrays!"
            ));

            return 0;
        }

        //Checks if array is of type INTEGER, if not reports error
        if(!Objects.equals(this.getJmmNodeType(ancestor, symbolTable), new Type("int", true))) {
            reportsArrayAccess.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Accessing arrays is only allowed on arrays!"
            ));

            return 0;
        }

        //Checks if array access index is of type INTEGER, if not reports error
        if(!Objects.equals(this.getJmmNodeType(index, symbolTable), new Type("int", false))) {
            reportsArrayAccess.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Index to access array is not of type integer!"
                ));

            return 0;
        }

        /*if (!Objects.equals(this.getJmmNodeType(ancestor, symbolTable).getName(), "int")) {
            reportsArrayAccess.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Accessing arrays is only allowed on arrays!"
            ));
        }*/

        return 0;
    }

    public Integer visitArray(JmmNode node, SymbolTable symbolTable) {
        var index = node.getJmmChild(0);
        var value = node.getJmmChild(1);
        var t1 = getJmmNodeType(index, symbolTable);
        var t2 = getJmmNodeType(index, symbolTable);

        if(!(Objects.equals(getJmmNodeType(index, symbolTable), new Type("int", false)))) {
            reportsArrayAccess.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Index to access array is not of type integer!"
                ));

            return 0;
        }
        else if(!(Objects.equals(getJmmNodeType(value, symbolTable), new Type("int", false)))) {
            reportsArrayAccess.add(
                new Report(
                        ReportType.ERROR,
                        Stage.SEMANTIC,
                        Integer.parseInt(node.get("lineStart")),
                        Integer.parseInt(node.get("colStart")),
                        "Value assigned to array position not of integer type!"
                ));
        }

        return 0;
    }
}
