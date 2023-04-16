package pt.up.fe.comp2023.Ollir;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp2023.symbol.table.MethodTable;
import pt.up.fe.comp2023.symbol.table.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OllirGenerator extends AJmmVisitor<StringBuilder, String> {

    private final SymbolTable symbolTable;
    private StringBuilder ollirCode;
    private String currentMethod = "";
    private Integer tempcounter = 1;
    private Boolean assign = false;
    private Symbol symbol = null;
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
        addVisit("Type", this::dealWithType);
        addVisit("Method", this::dealWithMethodDeclaration);
        addVisit("MainMethod", this::dealWithMainMethodDeclaration);
        addVisit("MainParam", this::dealWithMainParameter);
        addVisit("Ret", this::dealWithReturn);
        addVisit("Integer", this::dealWithInteger);
        addVisit("Boolean", this::dealWithBoolean);
        addVisit("Identifier", this::dealWithIdentifier);
        addVisit("Assign", this::dealWithAssign);
        addVisit("BinaryOp", this::dealWithBinaryOp);
        addVisit("NewObject", this::dealWithNewObject);
        addVisit("MethodCall", this::dealWithMethodCall);
        addVisit("Stmt", this::dealWithStatement);


    }

    private String dealWithStatement(JmmNode jmmNode, StringBuilder ollir) {
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollir);
        }
        this.ollirCode.append(";\n");
        return null;
    }

    private String dealWithMethodCall(JmmNode jmmNode, StringBuilder ollir) {

        /*for (JmmNode child : jmmNode.getChildren()) {
            dealWithArgumentOfMethod(child);
        }*/


        this.ollirCode.append("invokestatic(" + jmmNode.getChildren().get(0).get("id") + ", \"" + jmmNode.get("method") + "\", ");

        visit(jmmNode.getChildren().get(1), ollir);

        this.ollirCode.append(").V\n");

        return null;
    }

    private String dealWithNewObject(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append("new(" + jmmNode.get("id") + ")." +  jmmNode.get("id") + ";\n");
        this.ollirCode.append("invokespecial("+ jmmNode.getJmmParent().get("id")+"." + jmmNode.get("id") + ",\"<init>\").V;\n");


        return null;
    }

    private String dealWithBinaryOp(JmmNode jmmNode, StringBuilder ollir) {
        /*
        visit(jmmNode.getChildren().get(0), ollir);
        if(jmmNode.get("op").equals("&&") || jmmNode.get("op").equals("||"))
            this.ollirCode.append(" " + jmmNode.get("op") + ".bool ");
        else
            this.ollirCode.append(" " + jmmNode.get("op") + ".i32 ");

        visit(jmmNode.getChildren().get(1), ollir);
        return null;*/
        String type = getVariableType(symbol.getType(), ollir);
        var right = visit(jmmNode.getChildren().get(1), ollir) + type;
        var left = visit(jmmNode.getChildren().get(0), ollir) + type;
        if(jmmNode.getJmmParent().getKind().equals("Assign")){
            return left + " " + jmmNode.get("op") + type + " " + right + "\n";
        }

        this.ollirCode.append("temp" + tempcounter + type + " :=" + type + " " + left + " " + jmmNode.get("op") + type + " " + right + ";\n");
        tempcounter++;


        return "temp" + (tempcounter - 1);
    }

    private String dealWithAssign(JmmNode jmmNode, StringBuilder ollir) {

        //Check if is a parameter
        assign = true;
        this.tempcounter = 1;
        if(this.symbolTable.isParameter(this.currentMethod, jmmNode.get("id"))){
            this.symbol = this.symbolTable.getParameter(this.currentMethod, jmmNode.get("id"));

        }

        //Check if it is a field
        else if(this.symbolTable.isField(this.currentMethod,jmmNode.get("id"))){
            this.symbol = this.symbolTable.getField(this.currentMethod, jmmNode.get("id"));


        }

        if(jmmNode.getChildren().get(0).getKind().equals("NewObject")){
            String type = jmmNode.getChildren().get(0).get("id");
            this.ollirCode.append(jmmNode.get("id") + '.' + type + " :=." + type + " ");
            for (JmmNode child : jmmNode.getChildren()) {
                visit(child, ollirCode);

            }
            this.ollirCode.append(";\n");
            return null;
        }
        else{
            String temp = "";

            for (JmmNode child : jmmNode.getChildren()) {
                temp = visit(child, ollirCode);
            }

            String type = getVariableType(symbol.getType(), ollir);
            this.ollirCode.append(jmmNode.get("id") + type + " :=" + type + " " + temp + ";\n");

        }


        assign = false;
        return null;
    }
    private String dealWithIdentifier(JmmNode jmmNode, StringBuilder ollir) {
        Symbol symbol = null;

        //Check if is a parameter
        if(this.symbolTable.isParameter(this.currentMethod, jmmNode.get("id"))){
            symbol = this.symbolTable.getParameter(this.currentMethod, jmmNode.get("id"));

        }
        //Check if it is a field
        else if(this.symbolTable.isField(this.currentMethod,jmmNode.get("id"))){
            symbol = this.symbolTable.getField(this.currentMethod, jmmNode.get("id"));

        }
        if(assign){
            return jmmNode.get("id") + getVariableType(symbol.getType(), ollir);
        }
        this.ollirCode.append(jmmNode.get("id") + getVariableType(symbol.getType(), ollir));


        return null;
    }

    private String dealWithBoolean(JmmNode jmmNode, StringBuilder ollir) {
        if(assign){
            return jmmNode.get("value") + getVariableType(symbol.getType(), ollir);
        }
        this.ollirCode.append(jmmNode.get("value"));
        dealWithSimpleExpression(jmmNode, ollir);
        return null;
    }

    private String dealWithInteger(JmmNode jmmNode, StringBuilder ollir) {
        if(assign){
            return jmmNode.get("value") + getVariableType(symbol.getType(), ollir);
        }
        this.ollirCode.append(jmmNode.get("value"));
        dealWithSimpleExpression(jmmNode, ollir);
        return null;
    }


    private String dealWithReturn(JmmNode jmmNode, StringBuilder ollir) {
        Type returnType = this.symbolTable.getMethod(jmmNode.getJmmParent().get("name")).getReturnType();
        if(jmmNode.getChildren().get(0).getKind().equals("BinaryOp")){
            String type = getVariableType(returnType, ollir);
            this.ollirCode.append("temp" + tempcounter + type + ":=" + type +" ");
            this.ollirCode.append(jmmNode.getChildren().get(0).getChildren().get(0).get("id") + type + " " + jmmNode.getChildren().get(0).get("op") + type + " " + jmmNode.getChildren().get(0).getChildren().get(1).get("id") + type + ";\n");
            this.ollirCode.append("ret" + type + " temp" + tempcounter + type + ";\n");
            return null;
        }
        this.ollirCode.append("ret");

        this.ollirCode.append(getVariableType(returnType, ollir) + " ");
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        this.ollirCode.append(";\n");
        return null;
    }

    private String dealWithMainMethodDeclaration(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append("\n");
        this.ollirCode.append(".method public static main(args.array.String).V {\n");


        for (JmmNode child : jmmNode.getChildren()) {
            if(!child.getKind().equals("MainParam")){
                visit(child, ollirCode);
            }

        }
        this.ollirCode.append("\nret.V;\n}\n");
        return null;
    }

    private String dealWithMainParameter(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append("args.array.String");
        return null;
    }

    private String dealWithParameter(JmmNode jmmNode, StringBuilder ollir) {
        //GET FROM SYMBOL TABLE
        List<String> Parameters = new ArrayList<>();
        MethodTable table = this.symbolTable.getMethod(jmmNode.get("name"));

        if(table.getParameters().isEmpty()) return null;

        for(Symbol s: table.getParameters()){

            String param = s.getName() + getVariableType(s.getType(), ollir);
            Parameters.add(param);

        }

        this.ollirCode.append(String.join(", ", Parameters));
        return null;
    }
    private String dealWithMethodDeclaration(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append("\n");
        this.currentMethod = jmmNode.get("name");
        this.ollirCode.append(".method public");

        if(Objects.equals(jmmNode.get("name"), "main")){
            this.ollirCode.append(" static");
        }

        this.ollirCode.append(" " + jmmNode.get("name") + "(");

        dealWithParameter(jmmNode, ollir);

        this.ollirCode.append(")");

        Type returnType = this.symbolTable.getMethod(jmmNode.get("name")).getReturnType();
        this.ollirCode.append(getVariableType(returnType, ollir) + "{\n");


        for(JmmNode child: jmmNode.getChildren()) {
                visit(child, ollirCode);
        }

        

        this.ollirCode.append("}\n");


        return null;
    }

    private String dealWithVarDeclaration(JmmNode jmmNode, StringBuilder ollir) {
        if(jmmNode.getJmmParent().getKind().equals("Method") || jmmNode.getJmmParent().getKind().equals("MainMethod"))
            return null;

        if(!jmmNode.getJmmParent().getKind().equals("MethodDeclaration")){
            this.ollirCode.append(".field public " + jmmNode.get("var"));
        }

        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        this.ollirCode.append(";\n");
        return null;

    }

    private String dealWithType(JmmNode node, StringBuilder ollir) {
        if(node.getJmmParent().getKind().equals("Method")) return null;
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

    private String dealWithSimpleExpression(JmmNode node, StringBuilder ollir) {
        switch (node.getKind()){
            case "Integer":
                if(assign){
                    return ".i32";
                }
                this.ollirCode.append(".i32");
                break;
            case "Boolean":
                if(assign){
                    return ".bool";
                }
                this.ollirCode.append(".bool");
                break;
        }
        return null;
    }

    private String getVariableType(Type type, StringBuilder ollir) {
        String variableType = "";
        if(!type.isArray()){
            switch (type.getName()){
                case "int":
                    variableType = ".i32";
                    break;
                case "boolean":
                    variableType = ".bool";
                    break;

            }
        }
        return variableType;
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

        if(!Objects.equals(symbolTable.getSuper(), "")) {
            this.ollirCode.append(" extends ").append(symbolTable.getSuper());
        }

        this.ollirCode.append(" {\n");

        for(JmmNode child: node.getChildren()) {
            if(child.getKind().equals("VarDeclaration")){
                visit(child, ollirCode);
            }
        }

        this.ollirCode.append("\n");
        class_constructor(node);

        for(JmmNode child: node.getChildren()) {
            if(!child.getKind().equals("VarDeclaration")){
                visit(child, ollirCode);
            }
        }

        this.ollirCode.append("}\n");
        return null;

    }

    private void class_constructor(JmmNode node) {
        this.ollirCode.append(".construct " + symbolTable.getClassName() + "().V {\n");
        this.ollirCode.append("invokespecial(this, \"<init>\").V;\n");
        this.ollirCode.append("}\n");

    }

    private String defaultVisit(JmmNode jmmNode, StringBuilder ollir) {
        return null;
    }

    public static String formatString(String input) {
        String[] parts = input.substring(1, input.length() - 1).split(", ");
        return String.join(".", parts);
    }
    public String getTypeOfVariable(String variableName, String methodName) {
        MethodTable methodTable = this.symbolTable.getMethod(methodName);
        if(methodTable.getParameters().stream().anyMatch(x -> x.getName().equals(variableName))){
            return methodTable.getParameters().stream().filter(x -> x.getName().equals(variableName)).findFirst().get().getType().getName();
        }
        if(methodTable.getVariables().get(variableName) != null){

        }
        return null;
    }

}
