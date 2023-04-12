package pt.up.fe.comp2023.Ollir;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

public class OllirGenerator extends AJmmVisitor<Void, Void> {

    private final SymbolTable symbolTable;
    public OllirGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    @Override
    protected void buildVisitor() {

    }
}
