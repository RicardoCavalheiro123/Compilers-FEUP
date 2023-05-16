package pt.up.fe.comp2023.ollir;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp2023.semantics.symbol_table.MethodTable;
import pt.up.fe.comp2023.semantics.symbol_table.SymbolTable;

import java.util.*;

public class OllirGenerator extends AJmmVisitor<StringBuilder, String> {

    private final SymbolTable symbolTable;
    private StringBuilder ollirCode;
    private String currentMethod = "";
    private int temp_counter = 0;
    private int loop_counter = 0;
    private boolean returnable = false;
    private Symbol symbol = null;
    private String var_type = "";


    public String getOllirCode() {
        return ollirCode.toString();
    }

    public OllirGenerator(SymbolTable symbolTable, StringBuilder ollir) {

        this.symbolTable = symbolTable;
        this.ollirCode = ollir;

        this.buildVisitor();
    }
    // Visitor Methods
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
        addVisit("NewIntArray", this::dealWithNewIntArray);
        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("ArrayAssign", this::dealWithArrayAssign);
        addVisit("ArrayLength", this::dealWithArrayLength);
        addVisit("This", this::dealWithThis);
        addVisit("IfElse", this::dealWithIfElse);
        addVisit("Block", this::dealWithBlock);
        addVisit("While", this::dealWithWhile);
        addVisit("UnaryOp", this::dealWithUnaryOp);
        addVisit("Parenthesis", this::dealWithParentheses);


    }

    private String dealWithParentheses(JmmNode jmmNode, StringBuilder ollir) {
        // Boolean to return the children code instead of appending it
        returnable = true;
        String result = "";

        for (JmmNode child : jmmNode.getChildren()) {
            result = visit(child, ollir);

        }

        return result;

    }

    private String dealWithUnaryOp(JmmNode jmmNode, StringBuilder ollir){

        // Boolean to return the children code instead of appending it
        returnable = true;
        String result = "";
        // Visit the children
        for(int i = 0; i < jmmNode.getChildren().size(); i++){
            result = visit(jmmNode.getChildren().get(i), ollir);
        }
        this.ollirCode.append("temp" + temp_counter  + ".bool :=.bool !.bool " + result + ";\n");
        String temp = "temp" + temp_counter;
        temp_counter++;

        returnable = false;
        return temp + ".bool";
    }

    private String dealWithWhile(JmmNode jmmNode, StringBuilder ollir){

        String result = "";
        // Boolean to return the children code instead of appending it
        returnable = true;
        result = visit(jmmNode.getChildren().get(0), ollir);
        returnable = false;
        this.ollirCode.append("if (" + result + ") goto whilebody_"+ this.loop_counter +";\n");
        this.ollirCode.append("goto endwhile_" + this.loop_counter + ";\n");
        this.ollirCode.append("whilebody_" + this.loop_counter +":\n");
        this.loop_counter++;
        visit(jmmNode.getChildren().get(1), ollir);
        this.loop_counter--;
        this.ollirCode.append("if (" + result + ") goto whilebody_"+ this.loop_counter +";\n");
        this.ollirCode.append("endwhile_" + this.loop_counter + ":\n");
        return null;

    }

    private String dealWithBlock(JmmNode jmmNode, StringBuilder ollir) {

        for(JmmNode child : jmmNode.getChildren()){
            visit(child, ollir);
        }

        return null;
    }



    private String dealWithIfElse(JmmNode jmmNode, StringBuilder ollir) {
        String result = "";
        // Boolean to return the children code instead of appending it
        returnable = true;
        result = visit(jmmNode.getChildren().get(0), ollir);
        returnable = false;
        this.ollirCode.append("if (" + result + ") goto ifbody_"+ this.loop_counter +";\n");
        this.loop_counter++;
        visit(jmmNode.getChildren().get(2), ollir);
        this.ollirCode.append("goto endif_"+(this.loop_counter-1)+";\n");
        this.ollirCode.append("ifbody_"+(this.loop_counter-1)+":\n");
        visit(jmmNode.getChildren().get(1), ollir);
        this.ollirCode.append("endif_"+(this.loop_counter-1)+":\n");
        this.loop_counter--;
        return result;
    }

    private String dealWithThis(JmmNode jmmNode, StringBuilder ollir) {
        // If returnable is true, return the code instead of appending it
        if(returnable) return "this." + symbolTable.getClassName();
        this.ollirCode.append("this." + symbolTable.getClassName());
        return null;
    }

    private String dealWithArrayLength(JmmNode jmmNode, StringBuilder ollir) {
        String result = "";
        for(JmmNode child : jmmNode.getChildren()){
            result = visit(child, ollir);
        }
        this.ollirCode.append("temp" + temp_counter + ".i32" + " :=.i32 " + "arraylength(" + result + ")" + ".i32;\n");
        temp_counter++;
        return "temp" + (temp_counter -1) + ".i32";
    }

    private String dealWithArrayAssign(JmmNode jmmNode, StringBuilder ollir) {

        // Boolean to return the children code instead of appending it
        returnable = true;
        String result = "";
        String a = visit(jmmNode.getChildren().get(0), ollir);
        this.ollirCode.append("temp" + temp_counter + var_type + " :=" + var_type + " " + a + ";\n");
        String temp = "temp" + temp_counter;
        temp_counter++;

        // Visit the children
        for(int i = 0; i < jmmNode.getChildren().size(); i++){
            result = visit(jmmNode.getChildren().get(i), ollir);
        }
        this.ollirCode.append(jmmNode.get("id")+"[" + temp + var_type + "]" + var_type + " :=" + var_type + " " + result + ";\n");
        returnable = false;
        return result;
    }

    private String dealWithArrayAccess(JmmNode jmmNode, StringBuilder ollir) {
        String result = "";
        List<String> args = new ArrayList<>();
        for(JmmNode child : jmmNode.getChildren()){
            result = visit(child, ollir);
            args.add(result);
        }
        this.ollirCode.append("temp" + temp_counter + var_type + " :=" + var_type + " " + args.get(1) + var_type + ";\n");
        temp_counter++;
        this.ollirCode.append("temp" + temp_counter + var_type + " :=" + var_type + " " + jmmNode.getChildren().get(0).get("id") + "[" + "temp" + (temp_counter -1) + var_type + "]" + var_type + ";\n");
        temp_counter++;
        return "temp" + (temp_counter -1) + var_type;
    }

    private String dealWithNewIntArray(JmmNode jmmNode, StringBuilder ollir) {

        String result = "";
        for(JmmNode child : jmmNode.getChildren()){
            result = visit(child, ollir);
        }


        this.ollirCode.append("temp" + temp_counter + var_type + " :=" + var_type + " " + result + ";\n");
        temp_counter++;
        return "new(array, temp" + (temp_counter -1) + var_type + ").array" + var_type;


    }
    private String dealWithStatement(JmmNode jmmNode, StringBuilder ollir) {
        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollir);
        }
        this.ollirCode.append(";\n");
        return null;
    }

    private String dealWithMethodCall(JmmNode jmmNode, StringBuilder ollir) {


        returnable = true;
        List<String> args = new ArrayList<>();

        for (int i = 1; i < jmmNode.getChildren().size(); i++) {
            args.add("," + visit(jmmNode.getChildren().get(i), ollir));
        }

        String result = String.join("",args);
        String var = "";
        if(jmmNode.getChildren().get(0).getOptional("id").isPresent()) var = "id";
        else if(jmmNode.getChildren().get(0).getOptional("value").isPresent()) var = "value";
        else if(jmmNode.getChildren().get(0).getKind().equals("This")){
            this.ollirCode.append("temp" + temp_counter + var_type + " :=" + var_type + " ");
            temp_counter++;
            this.ollirCode.append("invokevirtual(" + "this" +"," + "\"" + jmmNode.get("method")+ "\"" +result+ ")" + var_type + ";\n");
            return "temp" + (temp_counter -1) + var_type;

        }

        if(jmmNode.getJmmParent().getKind().equals("BinaryOp") || jmmNode.getJmmParent().getKind().equals("MethodCall")){
            this.ollirCode.append("temp" + temp_counter + var_type + " :=" + var_type + " ");
            String res = visit(jmmNode.getChildren().get(0), ollir);
            this.ollirCode.append("invokevirtual(" + res +"," + "\"" + jmmNode.get("method")+ "\"" +result+ ")" + var_type + ";\n");
            temp_counter++;
            return "temp" + (temp_counter -1) + var_type;
        }

        if(jmmNode.getChildren().get(0).getOptional(var).isPresent() && is_Static(jmmNode.getChildren().get(0).getOptional(var).get())){
            if(jmmNode.getJmmParent().getKind().equals("Assign")){
                return "invokestatic(" + jmmNode.getChildren().get(0).get(var) +"," + "\"" + jmmNode.get("method")+ "\"" +result+ ").V";
            }

            this.ollirCode.append("invokestatic(" + jmmNode.getChildren().get(0).get(var) +"," + "\"" + jmmNode.get("method")+ "\"" +result+ ").V");
        }
        else{
            String return_type = getReturnOfMethod(jmmNode.get("method"));
            var_type = return_type;
            if(jmmNode.getJmmParent().getKind().equals("Assign")){
                if(jmmNode.getChildren().get(0).getKind().equals("Parenthesis")) {
                    String res = visit(jmmNode.getChildren().get(0).getChildren().get(0), ollir);

                    return "invokevirtual(" + res + "," + "\"" + jmmNode.get("method") + "\"" + result + ")" + return_type;
                }
                else
                    return "invokevirtual(" + jmmNode.getChildren().get(0).get(var) + getTypeOfVariable(this.currentMethod,jmmNode.getChildren().get(0).get(var) ) +"," + "\"" + jmmNode.get("method")+ "\"" +result+ ")" + return_type + "";
            }
            else if(jmmNode.getJmmParent().getKind().equals("Stmt")) {
                result = visit(jmmNode.getChildren().get(0), ollir);
                this.ollirCode.append("invokevirtual(" + result + "," + "\"" + jmmNode.get("method") + "\"" + ")" + return_type + "");
                return null;
            }
            if(returnable){
                result = visit(jmmNode.getChildren().get(0), ollir);
                this.ollirCode.append("temp" + temp_counter + return_type + " :=" + return_type + " invokevirtual(" + result  +"," + "\"" + jmmNode.get("method")+ "\"" + ")" + return_type + ";\n");
                temp_counter++;
                return "temp" + (temp_counter -1) + return_type;

            }
            String res = visit(jmmNode.getChildren().get(0), ollir);
            this.ollirCode.append("invokevirtual(" + res +"," + "\"" + jmmNode.get("method")+ "\"" +result+ ")" + return_type + "");
        }


        returnable = false;
        return null;
    }

    private String getReturnOfMethod(String method) {
        try {
            return getVariableType(this.symbolTable.getMethod(method).getReturnType(), new StringBuilder());
        } catch (Exception e) {
            return ".V";
        }
    }

    private String dealWithNewObject(JmmNode jmmNode, StringBuilder ollir) {

        if(jmmNode.getJmmParent().getKind().equals("Assign")){
            this.ollirCode.append(jmmNode.getJmmParent().get("id"));
        }
        else{
            this.ollirCode.append("temp" + temp_counter);
            temp_counter++;
        }
        this.ollirCode.append("." +jmmNode.get("id") + " :=." + jmmNode.get("id") + " new(" + jmmNode.get("id") + ")." + jmmNode.get("id") + ";\n");
        this.ollirCode.append("invokespecial(");
        String ex = "";
        if(jmmNode.getJmmParent().getKind().equals("Assign")){
            this.ollirCode.append(jmmNode.getJmmParent().get("id"));
            ex = jmmNode.getJmmParent().get("id") + "." + jmmNode.get("id");
        }
        else{
            this.ollirCode.append("temp" + (temp_counter - 1));
            ex = "temp" + (temp_counter - 1) + "." + jmmNode.get("id");
        }
        this.ollirCode.append("." + jmmNode.get("id") + ",\"<init>\").V");
        if(jmmNode.getJmmParent().getKind().equals("Parenthesis")){
            this.ollirCode.append(";\n");
        }


        return ex;
    }

    private String dealWithBinaryOp(JmmNode jmmNode, StringBuilder ollir) {

        List<String> Comp_Op = Arrays.asList("<", ">", "<=", ">=", "==", "!=", "&&", "||");

        // Visit right node
        var right = visit(jmmNode.getChildren().get(1), ollir);

        // Visit left node
        var left = visit(jmmNode.getChildren().get(0), ollir);
        String result_type = "";


        if(Comp_Op.contains(jmmNode.get("op"))) result_type = ".bool";
        else result_type = ".i32";

        if(jmmNode.getJmmParent().getKind().equals("Assign") || jmmNode.getJmmParent().getKind().equals("NewIntArray")){
            return left + " " + jmmNode.get("op") + result_type + " " + right;
        }


        this.ollirCode.append("temp" + temp_counter + result_type + " :=" + result_type + " " + left + " " + jmmNode.get("op") + result_type + " " + right + ";\n");


        temp_counter++;


        return "temp" + (temp_counter - 1) + result_type;
    }

    private String dealWithAssign(JmmNode jmmNode, StringBuilder ollir) {
        int isparameter = -1;

        // Boolean to return the children code instead of appending it
        returnable = true;

        boolean fieldOfClass = false;


        //Check if it is a local variable
        if(this.symbolTable.isLocalVar(this.currentMethod,jmmNode.get("id"))){
            this.symbol = this.symbolTable.getLocalVar(this.currentMethod, jmmNode.get("id"));
            if(symbol.getType().isArray()) {
                var_type = ".array" + getVariableType(symbol.getType(), ollir);
            }
            else{
                var_type = getVariableType(symbol.getType(), ollir);
            }

        }

        //Check if is a parameter
        else if(this.symbolTable.isParameter(this.currentMethod, jmmNode.get("id"))){
            this.symbol = this.symbolTable.getParameter(this.currentMethod, jmmNode.get("id"));
            var_type = getVariableType(symbol.getType(), ollir);
            isparameter = this.symbolTable.getParameterIndex(this.currentMethod, jmmNode.get("id")) + 1;
        }

        //Check if it is a field of the class
        else if(this.symbolTable.isFieldOfClass(jmmNode.get("id"))){
            this.symbol = this.symbolTable.getFieldOfClass(jmmNode.get("id"));
            if(symbol.getType().isArray()) {
                var_type = ".array" + getVariableType(symbol.getType(), ollir);
            }
            else{
                var_type = getVariableType(symbol.getType(), ollir);
            }
            fieldOfClass = true;

        }
        if(jmmNode.getChildren().get(0).getKind().equals("NewObject")){

            for (JmmNode child : jmmNode.getChildren()) {
                visit(child, ollirCode);

            }
            this.ollirCode.append(";\n");
            returnable = false;
            return null;
        }
        else if(jmmNode.getChildren().get(0).getKind().equals("MethodCall")){
            String result = "";
            for (JmmNode child : jmmNode.getChildren()) {
                result = visit(child, ollirCode);

            }
            String type = getReturnOfMethod(jmmNode.getChildren().get(0).get("method"));
            this.ollirCode.append(jmmNode.get("id") + type + " :=" + type + " " + result);
            this.ollirCode.append(";\n");
            return null;
        }
        else{
            String temp = "";

            for (JmmNode child : jmmNode.getChildren()) {
                temp = visit(child, ollirCode);
            }
            String type = getVariableType(symbol.getType(), ollir);

            // Append $ if it is a parameter
            if(isparameter!=-1){
                this.ollirCode.append("$" + isparameter + ".");

            }

            else if(fieldOfClass){
                if(jmmNode.getChildren().get(0).getKind().equals("Identifier") || jmmNode.getChildren().get(0).getKind().equals("Integer") || jmmNode.getChildren().get(0).getKind().equals("Boolean")){
                    this.ollirCode.append("putfield(this," + jmmNode.get("id") + type + "," + temp + ").V;\n");
                    return null;
                }
                this.ollirCode.append("temp" + temp_counter + type + " :=" + type + " " + temp + ";\n");
                this.ollirCode.append("putfield(this," + jmmNode.get("id") + type + "," + "temp" + temp_counter + type + ").V;\n");
                temp_counter++;
                return null;
            }


            this.ollirCode.append(jmmNode.get("id") + type + " :=" + type + " " + temp + ";\n");


        }


        returnable = false;
        return null;
    }
    private String dealWithIdentifier(JmmNode jmmNode, StringBuilder ollir) {

        int parameter = -1;

        //Check if it is a local variable
        if(this.symbolTable.isLocalVar(this.currentMethod,jmmNode.get("id"))){
            symbol = this.symbolTable.getLocalVar(this.currentMethod, jmmNode.get("id"));
            var_type = getVariableType(symbol.getType(), ollir);

        }

        //Check if is a parameter
        else if(this.symbolTable.isParameter(this.currentMethod, jmmNode.get("id"))){
            symbol = this.symbolTable.getParameter(this.currentMethod, jmmNode.get("id"));
            var_type = getVariableType(symbol.getType(), ollir);
            parameter = this.symbolTable.getParameterIndex(this.currentMethod, jmmNode.get("id")) + 1;

        }

        //Check if it is a field of the class
        else if(this.symbolTable.isFieldOfClass(jmmNode.get("id"))){
            symbol = this.symbolTable.getFieldOfClass(jmmNode.get("id"));
            var_type = getVariableType(symbol.getType(), ollir);
            this.ollirCode.append("temp" + temp_counter + var_type + ":=" + var_type + " getfield(this," + jmmNode.get("id") + var_type + ")" + var_type + ";\n");
            temp_counter++;
            return "temp" + (temp_counter -1) + var_type;
        }
        String res = "";
        if(parameter != -1){
            res += "$" + parameter + ".";
        }

        if(returnable){
            return res + jmmNode.get("id") + var_type;
        }
        this.ollirCode.append(res + jmmNode.get("id") + getVariableType(symbol.getType(), ollir));


        return null;
    }

    private String dealWithBoolean(JmmNode jmmNode, StringBuilder ollir) {
        this.var_type = ".bool";
        int value = jmmNode.get("value").equals("true") ? 1 : 0;

        // If returnable is true then return the value instead of appending it to the ollir code
        if(returnable){
            return value + var_type;
        }
        this.ollirCode.append(value);
        dealWithSimpleExpression(jmmNode, ollir);
        return null;
    }

    private String dealWithInteger(JmmNode jmmNode, StringBuilder ollir) {
        this.var_type = ".i32";

        // If returnable is true then return the value instead of appending it to the ollir code
        if(returnable){
            return jmmNode.get("value") + var_type;
        }
        this.ollirCode.append(jmmNode.get("value"));
        dealWithSimpleExpression(jmmNode, ollir);
        return null;
    }


    private String dealWithReturn(JmmNode jmmNode, StringBuilder ollir) {
        String temp = "";
        Type returnType = this.symbolTable.getMethod(jmmNode.getJmmParent().get("name")).getReturnType();
        //Get id or get value if one in null
        JmmNode child = jmmNode.getChildren().get(0);
        String val = "";
        if(child.getOptional("id").isPresent()){
            val = "id";
        }
        else if(child.getOptional("value").isPresent()){
            val = "value";
        }
        //Check if is a parameter
        if(!val.equals("")){

            //Check if it is a local variable
            if(this.symbolTable.isLocalVar(this.currentMethod,child.get(val)) && !val.equals("")){
                this.symbol = this.symbolTable.getLocalVar(this.currentMethod, child.get(val));

            }
            //Check if it is a parameter
            else if(this.symbolTable.isParameter(this.currentMethod, child.get(val))){
                this.symbol = this.symbolTable.getParameter(this.currentMethod, child.get(val));

            }
            else {
                var_type = getVariableType(returnType, ollir);
            }
        }
        else {
            var_type = getVariableType(returnType, ollir);
        }
        returnable = true;

        temp = visit(jmmNode.getChildren().get(0), ollir);


        this.ollirCode.append("ret" + getVariableType(returnType, ollir) + " " + temp + ";\n");

        returnable = false;
        return null;
    }

    private String dealWithMainMethodDeclaration(JmmNode jmmNode, StringBuilder ollir) {
        this.ollirCode.append("\n");
        this.ollirCode.append(".method public static main(args.array.String).V {\n");

        this.currentMethod = "main";

        for (JmmNode child : jmmNode.getChildren()) {
            if(!child.getKind().equals("MainParam")){
                visit(child, ollirCode);
            }

        }
        this.ollirCode.append("ret.V;\n}\n");
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
            this.ollirCode.append(".field " + jmmNode.get("var"));
        }

        for (JmmNode child : jmmNode.getChildren()) {
            visit(child, ollirCode);
        }
        this.ollirCode.append(";\n");
        return null;

    }

    private String dealWithType(JmmNode node, StringBuilder ollir) {
        if(node.getJmmParent().getKind().equals("Method")) return null;

        if(node.get("isArray").equals("true")){
            this.ollirCode.append(".array");
        }
        switch (node.getKind()){
            case "IntType":
                this.ollirCode.append(".i32");
                break;
            case "BooleanType":
                this.ollirCode.append(".bool");
                break;
            default:
                var n = node.getJmmParent().get("var");
                Symbol s = null;
                if(this.symbolTable.isLocalVar(this.currentMethod, n)) s = this.symbolTable.getLocalVar(this.currentMethod, n);
                if(s == null && this.symbolTable.isParameter(this.currentMethod, n)) s = this.symbolTable.getParameter(this.currentMethod, n);
                if(s == null && this.symbolTable.isFieldOfClass(n)) s = this.symbolTable.getFieldOfClass(n);
                if(s != null) this.ollirCode.append("." + s.getType().getName());
                break;
        }
        return null;
    }

    private String dealWithSimpleExpression(JmmNode node, StringBuilder ollir) {
        switch (node.getKind()){
            case "Integer":
                if(returnable){
                    return ".i32";
                }
                this.ollirCode.append(".i32");
                break;
            case "Boolean":
                if(returnable){
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
                default:
                    variableType = "." +type.getName();
                    break;

            }
        }
        else {
            variableType = ".array.";
            switch (type.getName()){
                case "int":
                    variableType += "i32";
                    break;
                case "boolean":
                    variableType += "bool";
                    break;
                default:
                    variableType += type.getName();
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
        MethodTable methodTable = this.symbolTable.getMethod(variableName);

        for(Symbol t : methodTable.getVariables().keySet()){
            if(t.getName().equals(methodName)){
                return "." + t.getType().getName();
            }
        }

        for(Symbol t : methodTable.getParameters()){
            if(t.getName().equals(methodName)){
                return "." + t.getType().getName();
            }
        }

        for(Symbol t : this.symbolTable.getFields()){
            if(t.getName().equals(methodName)){
                return "." + t.getType().getName();
            }
        }

        return null;
    }

    public Boolean is_Static(String name) {
        if(this.symbolTable.isParameter(this.currentMethod, name) ||
                this.symbolTable.isLocalVar(this.currentMethod, name) ||
                this.symbolTable.isFieldOfClass(name)) {
            return false;
        }
        return true;
    }
}

