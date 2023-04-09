package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.ClassUnit;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleJasmin implements JasminBackend {

    List<Report> reports = new ArrayList<>();

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        ClassUnit resultOllirClass = ollirResult.getOllirClass();

        try {
            String jasminCode = getJasminString(resultOllirClass);

            return new JasminResult(ollirResult, jasminCode, this.reports);
        } catch (Exception e) {
            return new JasminResult(resultOllirClass.getClassName(), null, Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Error while generating Jasmin code", e)));
        }
    }

    private String getJasminString(ClassUnit resultOllirClass) {
        String jasminCode = "";

        jasminCode += getClassJasminString(resultOllirClass);
        jasminCode += getMethodsJasminString(resultOllirClass);

        return jasminCode;
    }

    private String getMethodsJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();
        for (int i = 0; i < resultOllirClass.getMethods().size(); i++) {
            jasminCodeBuilder.append(".method public ").append(resultOllirClass.getMethods().get(i).getMethodName()).append("(");

            for (int j = 0; j < resultOllirClass.getMethods().get(i).getParams().size(); j++) {
                jasminCodeBuilder.append(resultOllirClass.getMethods().get(i).getParams().get(j).getType());
            }

            jasminCodeBuilder.append(")").append(resultOllirClass.getMethods().get(i).getReturnType()).append(" {\n");
            jasminCodeBuilder.append(".limit stack " + (resultOllirClass.getMethods().get(i).getEndNode().getId() - resultOllirClass.getMethods().get(i).getBeginNode().getId()) + "\n");
            jasminCodeBuilder.append(".limit locals " + resultOllirClass.getMethods().get(i).getVarTable().size() + "\n");

            for (int j = 0; j < resultOllirClass.getMethods().get(i).getVarTable().size(); j++) {
                jasminCodeBuilder.append(".var ").append(j).append(" is ").append(resultOllirClass.getMethods().get(i).getVarTable().get(j).toString()).append(" ").append(resultOllirClass.getMethods().get(i).getVarTable().get(j).getVarType()).append(" from ").append(resultOllirClass.getMethods().get(i).getVarTable().get(j).getVirtualReg()).append(" to ").append(resultOllirClass.getMethods().get(i).getVarTable().get(j).getVirtualReg()).append("\n");
            }

            jasminCodeBuilder.append("   ").append(resultOllirClass.getMethods().get(i).getInstructions()).append("\n");
            jasminCodeBuilder.append("}\n\n");
        }
        return jasminCodeBuilder.toString();
    }

    private String getClassJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(
                ".class " + resultOllirClass.getClass().getSimpleName() + "\n" +
                ".super " + resultOllirClass.getSuperClass() + "\n\n" +
                ".method public <init>()V\n" +
                "   aload_0\n" +
                "   invokenonvirtual " + resultOllirClass.getSuperClass() + "/<init>()V\n" +
                "   return\n" +
                ".end method\n\n"
        );

        jasminCodeBuilder.append(getFieldsJasminString(resultOllirClass));

        return jasminCodeBuilder.toString();
    }

    private String getFieldsJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (int i = 0; i < resultOllirClass.getFields().size(); i++) {
            jasminCodeBuilder.append(".field public static ").append(resultOllirClass.getFields().get(i).getFieldName()).append(" ").append(resultOllirClass.getFields().get(i).getFieldType()).append("\n");
        }

        return jasminCodeBuilder.toString();
    }
}
