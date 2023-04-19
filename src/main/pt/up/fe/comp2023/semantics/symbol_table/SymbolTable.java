package pt.up.fe.comp2023.semantics.symbol_table;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class SymbolTable implements pt.up.fe.comp.jmm.analysis.table.SymbolTable {
    public String className = "";
    public String superName = "";

    public List<String> imports = new ArrayList<>();
    public HashMap<Symbol, Boolean> fields = new HashMap<>();

    public MethodTable getMethod(String method) {
        return methods.get(method);
    }

    public HashMap<String, MethodTable> methods = new HashMap<>();


    @Override
    public List<String> getImports() { return imports; }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSuper() {
        return superName;
    }

    @Override
    public List<Symbol> getFields() {
        return new ArrayList<>(fields.keySet());
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<>(methods.keySet());
    }

    @Override
    public Type getReturnType(String s) {
        return methods.get(s).getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String s) {
        return methods.get(s).getParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String s) {

        return methods.get(s).getLocalVariables();
    }

    public Boolean isAssigned(Symbol symbol){
        return fields.get(symbol);
    }

    public MethodTable findMethod(String methodName) {
        return methods.get(methodName);
    }

    // Print the symbol table
    public String toString() {
        return print();
    }

    //Check if it is a parameter
    public Boolean isParameter(String method, String id){
        if(!methods.containsKey(method))
            return false;
        if(methods.get(method).getParameters() == null)
            return false;
        return methods.get(method).getParameters().stream().anyMatch(symbol -> symbol.getName().equals(id));
    }

    //Get the parameter
    public Symbol getParameter(String method, String id){
        return methods.get(method).getParameters().stream().filter(symbol -> symbol.getName().equals(id)).findFirst().get();
    }


    public boolean isFieldOfMethod(String method, String id) {
        if(!methods.containsKey(method))
            return false;
        if(methods.get(method).getVariables() == null)
            return false;
        return methods.get(method).getVariables().keySet().stream().anyMatch(symbol -> symbol.getName().equals(id));

    }

    public Symbol getFieldOfMethod(String currentMethod, String id) {
        return methods.get(currentMethod).getLocalVariables().stream().filter(symbol -> symbol.getName().equals(id)).findFirst().get();
    }

    public boolean isFieldOfClass(String id) {
        return fields.keySet().stream().anyMatch(symbol -> symbol.getName().equals(id));
    }

    public Symbol getFieldOfClass(String id) {
        return fields.keySet().stream().filter(symbol -> symbol.getName().equals(id)).findFirst().get();
    }
}
