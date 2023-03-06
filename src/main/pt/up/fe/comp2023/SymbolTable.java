package pt.up.fe.comp2023;

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
        return methods.get(s).getReturnType("");
    }

    @Override
    public List<Symbol> getParameters(String s) {
        return methods.get(s).getParameters("");
    }

    @Override
    public List<Symbol> getLocalVariables(String s) {
        return methods.get(s).getLocalVariables("");
    }
}
