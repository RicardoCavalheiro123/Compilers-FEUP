package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.backend.instructions.CallToJasmin;

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

        jasminCode += "; class with syntax accepted by jasmin 2.3\n\n";

        jasminCode += getClassJasminString(resultOllirClass);
        jasminCode += getMethodsJasminString(resultOllirClass);

        return jasminCode;
    }

    private String getClassJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();
        String superClassName = resultOllirClass.getSuperClass() == null ? "java/lang/Object" : resultOllirClass.getSuperClass();
        jasminCodeBuilder.append(
                ".class public " + resultOllirClass.getClassName() + "\n" +
                        ".super " + superClassName + "\n\n");

        jasminCodeBuilder.append(";\n; standard initializer\n");

        jasminCodeBuilder.append(
                        ".method public <init>()V\n" +
                        "   aload_0\n\n" +
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

        for (Method method : resultOllirClass.getMethods()) {
            jasminCodeBuilder.append(".method ").append(method.getMethodAccessModifier().toString().toLowerCase()).append(" ");

            if (method.isStaticMethod()) jasminCodeBuilder.append("static ");
            if (method.isFinalMethod()) jasminCodeBuilder.append("final ");

            jasminCodeBuilder.append(method.getMethodName()).append("(");

            for (Element param : method.getParams()) {
                jasminCodeBuilder.append(param.getType().toString()).append(";");
            }
            jasminCodeBuilder.append(")").append(method.getReturnType()).append("\n");

            StringBuilder instructions = new StringBuilder();
            for (Instruction instruction : method.getInstructions()) {
                instructions.append("   ").append(getInstructionJasminString(method, instruction)).append("\n");
            }

            jasminCodeBuilder.append("  .limit stack " + "99" + "\n");
            jasminCodeBuilder.append("  .limit locals " + "99" + "\n\n");

            jasminCodeBuilder.append(instructions);

            jasminCodeBuilder.append(method.getInstructions()).append("\n");
            jasminCodeBuilder.append(".end method\n\n");
        }

        return jasminCodeBuilder.toString();
    }

    private String getInstructionJasminString(Method method, Instruction instruction) {
        String jasminCode = "";

        switch (instruction.getInstType()) {
            case ASSIGN -> jasminCode += getAssignJasminString(method, (AssignInstruction) instruction);
            case CALL -> jasminCode += getCallJasminString(method, (CallInstruction) instruction);
            case GOTO -> jasminCode += getGotoJasminString(method, (GotoInstruction) instruction);
            case BRANCH ->  jasminCode += getBranchJasminString(method, (CondBranchInstruction) instruction);
            case RETURN -> jasminCode += getReturnJasminString(method, (ReturnInstruction) instruction);
            case PUTFIELD -> jasminCode += getPutFieldJasminString(method, (PutFieldInstruction) instruction);
            case GETFIELD -> jasminCode += getGetFieldJasminString(method, (GetFieldInstruction) instruction);
            case UNARYOPER -> jasminCode += getUnaryOperJasminString(method, (UnaryOpInstruction) instruction);
            case BINARYOPER -> jasminCode += getBinaryOperJasminString(method, (BinaryOpInstruction) instruction);
            case NOPER -> jasminCode += getNOperJasminString(method, (SingleOpInstruction) instruction);
        }

        return jasminCode;
    }

    private String getAssignJasminString(Method method, AssignInstruction instruction) {
        String jasminCode = "";



        return jasminCode;
    }

    private String getCallJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        switch (instruction.getInvocationType()) {
            case invokevirtual -> jasminCode += CallToJasmin.getVirtualCallJasminString(method, instruction);
            case invokeinterface -> jasminCode += CallToJasmin.getInterfaceCallJasminString(method, instruction);
            case invokespecial -> jasminCode += CallToJasmin.getSpecialCallJasminString(method, instruction);
            case invokestatic -> jasminCode += CallToJasmin.getStaticCallJasminString(method, instruction);
            case NEW -> jasminCode += CallToJasmin.getNewJasminString(method, instruction);
            case arraylength -> jasminCode += CallToJasmin.getArrayLengthJasminString(method, instruction);
            case ldc -> jasminCode += CallToJasmin.getLdcJasminString(method, instruction);
        }

        return jasminCode;
    }

    private String getGotoJasminString(Method method, GotoInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getBranchJasminString(Method method, CondBranchInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getReturnJasminString(Method method, ReturnInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getPutFieldJasminString(Method method, PutFieldInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getGetFieldJasminString(Method method, GetFieldInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getUnaryOperJasminString(Method method, UnaryOpInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getBinaryOperJasminString(Method method, BinaryOpInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    private String getNOperJasminString(Method method, SingleOpInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }
}
