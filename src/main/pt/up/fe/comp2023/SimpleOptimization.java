package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp2023.ollir.OllirGenerator;
import pt.up.fe.comp2023.optimization.ConstantFoldingVisitor;
import pt.up.fe.comp2023.optimization.ConstantPropagationVisitor;
import pt.up.fe.comp2023.optimization.RegisterAllocation;
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
                changed = constantPropagation.visit(semanticsResult.getRootNode(), true);

                var constantFolding = new ConstantFoldingVisitor();
                changed = constantFolding.visit(semanticsResult.getRootNode(), true) || changed;

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

    public OllirResult optimize(OllirResult ollirResult) {
        if(ollirResult.getConfig().containsKey("registerAllocation")) {
            int register_number = Integer.parseInt(ollirResult.getConfig().get("registerAllocation"));

            if(register_number != -1) {
                ollirResult.getOllirClass().buildCFGs();
                ollirResult.getOllirClass().buildVarTables();

                new RegisterAllocation().allocation(ollirResult.getOllirClass(), register_number);
            }
        }

        return ollirResult;
    }
}
