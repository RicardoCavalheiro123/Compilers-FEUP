package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.Objects;

public class ArrayAccessAnalyzer extends PreorderVisitor {
    public ArrayAccessAnalyzer() {
        super();
        //addVisit("ArrayAccess", this::visitArray);
        //setDefaultVisit(this::defVisit);
    }

    public Integer defVisit(JmmNode node, SymbolTable symbolTable) {
        return null;
    }

    public Integer visit(JmmNode node, SymbolTable symbolTable) {
        var ancestor = node.getJmmChild(0);
        var n = node.getJmmChild(1);

        //If first is not an array and is trying to be accessed as an array, report error
        if(!(this.getJmmNodeType(ancestor, symbolTable)).isArray()) {
            addReport(new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("col")),
                    "Accessing arrays is only allowed on arrays!"
            ));
        }

        //Checks if array access index is of type INTEGER, if not reports error
        if(!Objects.equals(this.getJmmNodeType(ancestor, symbolTable), new Type("int", false))) {
            addReport(new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("col")),
                    "Index to access array must be type Integer!"
            ));
        }

        return 0;
    }
}
