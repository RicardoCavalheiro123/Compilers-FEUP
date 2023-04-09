package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.*;
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
    int limitStack, stack;

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

    private String getClassJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();
        String superClassName = resultOllirClass.getSuperClass() == null ? "java/lang/Object" : resultOllirClass.getSuperClass();
        jasminCodeBuilder.append(
                ".class public " + resultOllirClass.getClassName() + "\n" +
                        ".super " + superClassName + "\n\n" +
                        ".method public <init>()V\n" +
                        "   aload_0\n" +
                        "   invokenonvirtual " + superClassName + "/<init>()V\n" +
                        "   return\n" +
                        ".end method\n\n"
        );

        jasminCodeBuilder.append(getFieldsJasminString(resultOllirClass));

        return jasminCodeBuilder.toString();
    }

    private String getFieldsJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (Field field : resultOllirClass.getFields()) {
            jasminCodeBuilder.append(".field ").append(field.getFieldAccessModifier().toString().toLowerCase()).append(" ").append(field.getFieldName()).append(" ").append(field.getFieldType()).append("\n");
        }

        return jasminCodeBuilder.toString();
    }

    private String getMethodsJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();
        limitStack = 0;
        stack = 0;

        for (Method method : resultOllirClass.getMethods()) {
            jasminCodeBuilder.append(".method ").append(method.getMethodAccessModifier().toString().toLowerCase()).append(" ");

            if (method.isStaticMethod()) jasminCodeBuilder.append("static ");
            if (method.isFinalMethod()) jasminCodeBuilder.append("final ");

            jasminCodeBuilder.append(method.getMethodName()).append("(");

            for (Element param : method.getParams()) {
                jasminCodeBuilder.append(param.getType().toString());
            }
            jasminCodeBuilder.append(")").append(method.getReturnType()).append(" {\n");

            String instructions = "";
            for (Instruction instruction : method.getInstructions()) {
                instructions += getInstructionJasminString(method, instruction);
            }

            jasminCodeBuilder.append(".limit stack " + limitStack + "\n");
            jasminCodeBuilder.append(".limit locals " + method.getVarTable().size() + "\n");

            jasminCodeBuilder.append(instructions);

            jasminCodeBuilder.append("   ").append(method.getInstructions()).append("\n");
            jasminCodeBuilder.append("}\n\n");
        }

        return jasminCodeBuilder.toString();
    }

    private String getInstructionJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        switch (instruction.getInstType()) {
            case ASSIGN -> jasminCode += getAssignJasminString(method, instruction);
            case CALL -> jasminCode += getCallJasminString(method, instruction);
            case GOTO -> jasminCode += getGotoJasminString(method, instruction);
            case BRANCH ->  jasminCode += getBranchJasminString(method, instruction);
            case RETURN -> jasminCode += getReturnJasminString(method, instruction);
            case PUTFIELD -> jasminCode += getPutFieldJasminString(method, instruction);
            case GETFIELD -> jasminCode += getGetFieldJasminString(method, instruction);
            case UNARYOPER -> jasminCode += getUnaryOperJasminString(method, instruction);
            case BINARYOPER -> jasminCode += getBinaryOperJasminString(method, instruction);
            case NOPER -> jasminCode += getNOperJasminString(method, instruction);
        }

        return jasminCode;
    }

    private String getAssignJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getCallJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getGotoJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getBranchJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getReturnJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getPutFieldJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getGetFieldJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getUnaryOperJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getBinaryOperJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getNOperJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }
}
