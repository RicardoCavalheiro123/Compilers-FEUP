package pt.up.fe.comp2023.Ollir;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.util.List;
import java.util.Objects;

public class OllirGenerator extends AJmmVisitor<StringBuilder, String> {

    private final SymbolTable symbolTable;
    private StringBuilder ollirCode;

    public String getOllirCode() {
        return ollirCode.toString();
    }

    public OllirGenerator(SymbolTable symbolTable, StringBuilder ollir) {

        this.symbolTable = symbolTable;
        this.ollirCode = ollir;

        this.buildVisitor();
    }

    @Override
    protected void buildVisitor() {
        setDefaultVisit(this::defaultVisit);
        addVisit("Program", this::programVisit);
        addVisit("ClassDeclaration", this::dealWithClass);
        addVisit("ImportDeclaration", this::dealWithImport);
        addVisit("VarDeclaration", this::dealWithVarDeclaration);
        addVisit("Type", this::dealWithVariable);
        addVisit("Method", this::dealWithMethodDeclaration);
        addVisit("MainMethod", this::dealWithMainMethodDeclaration);
        addVisit("Parameter", this::dealWithParameter);
        addVisit("MainParam", this::dealWithMainParameter);
        addVisit("Ret", this::dealWithReturn);


    }

    private String dealWithType(JmmNode jmmNode, StringBuilder ollir){

        return null;
    }

    private String dealWithReturn(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append("return ");
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        return null;
    }

    private String dealWithMainMethodDeclaration(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append(".method public static main(");
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        this.ollirCode.append(").V {\nret.V;\n}\n");
        return null;
    }

    private String dealWithMainParameter(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append("args.array.String");
        return null;
    }

    private String dealWithParameter(JmmNode jmmNode, StringBuilder ollir) {
        //GET FROM SYMBOL TABLE

        MethodTable m = this.symbolTable.getMethod(jmmNode.get("name"));
        return null;
    }
    private String dealWithMethodDeclaration(JmmNode jmmNode, StringBuilder ollir) {

        this.ollirCode.append(".method public");

        if(Objects.equals(jmmNode.get("name"), "main")){
            this.ollirCode.append(" static");
        }

        this.ollirCode.append(" " + jmmNode.get("name") + "(");



        this.ollirCode.append("){\n");

        for(JmmNode child: jmmNode.getChildren()) {
            visit(child, ollirCode);
        }

        this.ollirCode.append("}\n");

        return null;
    }

    private String dealWithVarDeclaration(JmmNode jmmNode, StringBuilder ollir) {
        if(!jmmNode.getJmmParent().getKind().equals("MethodDeclaration")){
            this.ollirCode.append(".field public " + jmmNode.get("var"));
        }

        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        this.ollirCode.append(";\n");
        return null;

    }

    private String dealWithVariable(JmmNode node, StringBuilder ollir) {

        switch (node.getKind()){
            case "IntType":
                this.ollirCode.append(".i32");
                break;
            case "BooleanType":
                this.ollirCode.append(".bool");
                break;
        }
        return null;
    }

    private String programVisit(JmmNode jmmNode, StringBuilder ollir) {

        for (String s : symbolTable.getImports()) {
            this.ollirCode.append("import ").append(formatString(s)).append(";\n");
        }
        for(JmmNode child: jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        return null;
    }


    private String dealWithImport(JmmNode node, StringBuilder ollir) {
        return null;
    }

    private String dealWithClass(JmmNode node, StringBuilder ollir) {

        this.ollirCode.append(symbolTable.getClassName());

        if(symbolTable.getSuper() != null) {
            this.ollirCode.append(" extends ").append(symbolTable.getSuper());
        }

        this.ollirCode.append(" {\n");

        for(JmmNode child: node.getChildren()) {
            visit(child, ollirCode);
        }

        this.ollirCode.append("}\n");
        return null;

    }

    private String defaultVisit(JmmNode jmmNode, StringBuilder ollir) {
        return null;
    }

    public static String formatString(String input) {
        String[] parts = input.substring(1, input.length() - 1).split(", ");
        return String.join(".", parts);
    }

}
