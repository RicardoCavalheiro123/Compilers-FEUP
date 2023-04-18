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

public class MethodArgumentsCompatibilityAnalyzer extends SemanticVisitor {
    private List<Report> reportsMethodArguments;

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("MethodCall", this::visitMethodsArguments);
    }

    public MethodArgumentsCompatibilityAnalyzer() { this.reportsMethodArguments = new ArrayList<>(); }

    private Integer defaultVisit(JmmNode node, SymbolTable symbolTable) { return null; }

    public List<Report> getReports() { return this.reportsMethodArguments; }

    public Integer visitMethodsArguments(JmmNode node, SymbolTable symbolTable) {
        if(symbolTable.findMethod(node.get("method")) == null) {
            if(!Objects.equals(node.getJmmChild(0).getKind(), "This") ||
                    !Objects.equals(getJmmNodeType(node.getJmmChild(0), symbolTable), new Type(symbolTable.getClassName(), false))
            ) {
                return 0;
            }
        }


        var method_arguments = symbolTable.findMethod(node.get("method")).getParameters();
        var method_call_arguments_size = node.getChildren().size() - 1;

        if(method_arguments.size() != method_call_arguments_size) {
            reportsMethodArguments.add(
                new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("lineStart")),
                    Integer.parseInt(node.get("colStart")),
                    "Method call argument number does not correspond to method declaration!"
            ));
        }
        else {
            for(int i = 1; i <= method_arguments.size(); i++) {
                Type called_type = getJmmNodeType(node.getJmmChild(i), symbolTable);
                Type need_type = method_arguments.get(i - 1).getType();

                if(!compatibleType(called_type, need_type, symbolTable)) {
                    reportsMethodArguments.add(
                        new Report(
                            ReportType.ERROR,
                            Stage.SEMANTIC,
                            Integer.parseInt(node.get("lineStart")),
                            Integer.parseInt(node.get("colStart")),
                            "Method call argument does not match actual parameter type!"
                    ));

                    return 0;
                }
            }
        }

        return 0;
    }
}
