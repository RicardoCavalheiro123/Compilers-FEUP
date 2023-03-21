package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.ClassUnit;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;

import java.util.ArrayList;
import java.util.List;

public class SimpleJasmin implements JasminBackend {

    List<Report> reports = new ArrayList<>();

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        ClassUnit classUnit = ollirResult.getOllirClass();

        String jasminCode = getJasminString(classUnit);

        return new JasminResult(ollirResult, jasminCode, this.reports);
    }

    private String getJasminString(ClassUnit classUnit) {
        String jasminCode = "";

        jasminCode += ".class public " + classUnit.getClassName() + "\n.super" + classUnit.getSuperClass() + "\n\n;";


        return jasminCode;
    }
}
