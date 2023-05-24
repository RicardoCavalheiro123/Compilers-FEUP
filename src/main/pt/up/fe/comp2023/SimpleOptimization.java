package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.ollir.OllirGenerator;
import pt.up.fe.comp2023.optimization.ConstantFoldingVisitor;
import pt.up.fe.comp2023.optimization.ConstantPropagationVisitor;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleOptimization implements JmmOptimization {

    public HashMap<String, String> variables = new HashMap<>();

    @Override
    public JmmSemanticsResult optimize(JmmSemanticsResult semanticsResult) {
        boolean changed = true;
        String optimizeValue = semanticsResult.getConfig().getOrDefault("optimize", "false");

        if (optimizeValue.equals("true")) {
            while(changed) {
                var constantPropagation = new ConstantPropagationVisitor();
                //constantPropagation.variables = this.variables;
                changed = constantPropagation.visit(semanticsResult.getRootNode(), true);

                var constantFolding = new ConstantFoldingVisitor();
                //constantFolding.variables = constantPropagation.variables;
                changed |= constantFolding.visit(semanticsResult.getRootNode(), true);
                //this.variables = constantFolding.variables;

                var a = 0;
            }
        }

        return semanticsResult;
    }


    @Override
    public OllirResult toOllir(JmmSemanticsResult jmmSemanticsResult) {
        List<Report> reports = new ArrayList<>(jmmSemanticsResult.getReports());


        StringBuilder ollir = new StringBuilder();


        OllirGenerator visitor = new OllirGenerator((SymbolTable) jmmSemanticsResult.getSymbolTable(), ollir);

        JmmNode root = jmmSemanticsResult.getRootNode();

        visitor.visit(root, new StringBuilder());


        return new OllirResult(jmmSemanticsResult, visitor.getOllirCode(), reports);
    }

    @Override
    public OllirResult optimize(OllirResult ollirResult) {
        return ollirResult;
    }
}
