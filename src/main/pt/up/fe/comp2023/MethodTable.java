package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;

public class MethodTable implements pt.up.fe.comp.jmm.analysis.table.SymbolTable {
    List<Parameter> parameters;
    Type returnType;
    String className;
    HashMap<String, Boolean> variables = new HashMap<>();

    public MethodTable(Type returnType, List<Parameter> parameters, ) {
        returnType =
        className =
        variables = null;
    }

    @Override
    public List<String> getImports() {
        return null;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSuper() {
        return null;
    }

    @Override
    public List<Symbol> getFields() {
        return null;
    }

    @Override
    public List<String> getMethods() {
        return null;
    }

    @Override
    public Type getReturnType(String s) {
        return returnType;
    }

    @Override
    public List<Symbol> getParameters(String s) {
        return null;
    }

    @Override
    public List<Symbol> getLocalVariables(String s) {
        return variables;
    }
}
