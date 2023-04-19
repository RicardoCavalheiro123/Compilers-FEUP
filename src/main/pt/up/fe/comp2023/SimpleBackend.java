package pt.up.fe.comp2023;

import org.specs.comp.ollir.ClassUnit;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.backend.OllirToJasmin;

import java.util.ArrayList;
import java.util.List;

public class SimpleBackend implements JasminBackend {

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        List<Report> reports = new ArrayList<>(ollirResult.getReports());
        ClassUnit resultOllirClass = ollirResult.getOllirClass();

        try {
            OllirToJasmin ollirToJasmin = new OllirToJasmin();
            String jasminCode = ollirToJasmin.getJasminString(resultOllirClass);
            System.out.println(jasminCode);

            return new JasminResult(ollirResult, jasminCode, reports);
        } catch (Exception e) {
            return new JasminResult(resultOllirClass.getClassName(), null, List.of(Report.newError(Stage.GENERATION, -1, -1, "Error while generating Jasmin code", e)));
        }
    }
}
