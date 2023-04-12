package pt.up.fe.comp2023.analysers.typeVerification;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp2023.PreorderVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class OperandsTypeCompatibilityAnalyzer extends PreorderVisitor {

    public OperandsTypeCompatibilityAnalyzer() {
        super();

        //addVisit("BinaryOp", this::visitOperator);
    }

    public Integer visit(JmmNode node, SymbolTable symbolTable) {
        this.evaluateExpressionType(node, symbolTable);

        return 0;
    }
}
