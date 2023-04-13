package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp2023.backend.instructions.call.*;

import java.util.ArrayList;
import java.util.List;

public class SimpleJasmin implements JasminBackend {

    List<Report> reports = new ArrayList<>();
    String superClassName;

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        ClassUnit resultOllirClass = ollirResult.getOllirClass();
        superClassName = resultOllirClass.getSuperClass() == null ? "java/lang/Object" : resultOllirClass.getSuperClass();

        try {
            String jasminCode = getJasminString(resultOllirClass);

            return new JasminResult(ollirResult, jasminCode, this.reports);
        } catch (Exception e) {
            return new JasminResult(resultOllirClass.getClassName(), null, List.of(Report.newError(Stage.GENERATION, -1, -1, "Error while generating Jasmin code", e)));
        }
    }

    private String getJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(getClassJasminString(resultOllirClass));
        jasminCodeBuilder.append(getMethodsJasminString(resultOllirClass));

        return jasminCodeBuilder.toString();
    }

    private String getClassJasminString(ClassUnit resultOllirClass) {
        return ".class public " + resultOllirClass.getClassName() + "\n" +
                ".super " + superClassName + "\n\n" +
                getFieldsJasminString(resultOllirClass);
    }

    private String getFieldsJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (Field field : resultOllirClass.getFields()) {
            jasminCodeBuilder.append(".field ");

            if(field.getFieldAccessModifier() != AccessModifiers.DEFAULT) {
                jasminCodeBuilder.append(field.getFieldAccessModifier().toString().toLowerCase()).append(" ");
            }

            jasminCodeBuilder.append(field.getFieldName().toLowerCase()).append(" ")
                    .append(JasminUtils.typeCode(field.getFieldType()));

            if(field.isInitialized()) {
                jasminCodeBuilder.append(" = ").append(field.getInitialValue());
            }
            jasminCodeBuilder.append("\n");
        }

        return jasminCodeBuilder.toString();
    }

    private String getMethodsJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (Method method : resultOllirClass.getMethods()) {
            if (method.getMethodAccessModifier() == AccessModifiers.DEFAULT) {
                jasminCodeBuilder.append(".method public ").append("<init>").append("()V")
                    .append("\n\t").append("aload_0").append("\n\n\t")
                    .append("invokenonvirtual ")
                    .append(superClassName).append("/<init>()V\n").append("\treturn\n").append(".end method\n\n");

            } else {

                jasminCodeBuilder.append(".method ")
                        .append(method.getMethodAccessModifier().toString().toLowerCase())
                        .append(" ");

                if (method.isStaticMethod()) jasminCodeBuilder.append("static ");
                if (method.isFinalMethod()) jasminCodeBuilder.append("final ");

                jasminCodeBuilder.append(method.getMethodName()).append("(");

                for (Element param : method.getParams()) {
                    jasminCodeBuilder.append(JasminUtils.typeCode(param.getType()));
                }
                jasminCodeBuilder.append(")").append(JasminUtils.typeCode(method.getReturnType())).append("\n");

                StringBuilder instructions = new StringBuilder();
                for (Instruction instruction : method.getInstructions()) {
                    instructions.append("\t").append(getInstructionJasminString(method, instruction)).append("\n");
                }

                jasminCodeBuilder.append("\t.limit stack ").append("99").append("\n");
                jasminCodeBuilder.append("\t.limit locals ").append("99").append("\n\n");

                jasminCodeBuilder.append(instructions);

                jasminCodeBuilder.append(".end method\n\n");
            }
        }

        return jasminCodeBuilder.toString();
    }

    private String getInstructionJasminString(Method method, Instruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        switch (instruction.getInstType()) {
            case ASSIGN -> jasminCodeBuilder.append(getAssignJasminString(method, (AssignInstruction) instruction));
            case CALL -> jasminCodeBuilder.append(getCallJasminString(method, (CallInstruction) instruction));
            case GOTO -> jasminCodeBuilder.append(getGotoJasminString(method, (GotoInstruction) instruction));
            case BRANCH ->  jasminCodeBuilder.append(getBranchJasminString(method, (CondBranchInstruction) instruction));
            case RETURN -> jasminCodeBuilder.append(getReturnJasminString(method, (ReturnInstruction) instruction));
            case PUTFIELD -> jasminCodeBuilder.append(getPutFieldJasminString(method, (PutFieldInstruction) instruction));
            case GETFIELD -> jasminCodeBuilder.append(getGetFieldJasminString(method, (GetFieldInstruction) instruction));
            case UNARYOPER -> jasminCodeBuilder.append(getUnaryOperJasminString(method, (UnaryOpInstruction) instruction));
            case BINARYOPER -> jasminCodeBuilder.append(getBinaryOperJasminString(method, (BinaryOpInstruction) instruction));
            case NOPER -> jasminCodeBuilder.append(getNOperJasminString(method, (SingleOpInstruction) instruction));
        }

        return jasminCodeBuilder.toString();
    }

    private String getAssignJasminString(Method method, AssignInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        // TODO

        return jasminCodeBuilder.toString();
    }

    private String getCallJasminString(Method method, CallInstruction instruction) {
        InstructionCall callInstruction = null;

        switch (instruction.getInvocationType()) {
            case invokevirtual -> callInstruction = new InvokeVirtualInstruction();
            case invokeinterface -> callInstruction = new InvokeInterfaceInstruction();
            case invokespecial -> callInstruction = new InvokeSpecialInstruction();
            case invokestatic -> callInstruction = new InvokeStaticInstruction();
            case NEW -> callInstruction = new NewInstruction();
            case arraylength -> callInstruction = new ArrayLengthInstruction();
            case ldc -> callInstruction = new LdcInstruction();
        }

        return callInstruction.toJasmin(method, instruction);
    }

    private String getGotoJasminString(Method method, GotoInstruction instruction) {
        return "goto " + instruction.getLabel();
    }

    private String getBranchJasminString(Method method, CondBranchInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        // TODO

        return jasminCodeBuilder.toString();
    }

    private String getReturnJasminString(Method method, ReturnInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        if(instruction.hasReturnValue()) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getOperand()));
            jasminCodeBuilder.append("\n\t");
        }

        if(instruction.getOperand() != null) {
            ElementType type = instruction.getOperand().getType().getTypeOfElement();

            if(type == ElementType.INT32 || type == ElementType.BOOLEAN) {
                jasminCodeBuilder.append("i");
            } else {
                jasminCodeBuilder.append("a");
            }
        }

        jasminCodeBuilder.append("return");

        return jasminCodeBuilder.toString();
    }

    private String getPutFieldJasminString(Method method, PutFieldInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstOperand()));
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getThirdOperand()));
        jasminCodeBuilder.append("\n\t");

        jasminCodeBuilder.append("putfield ");
        jasminCodeBuilder.append(instruction.getFirstOperand().getClass().getName()).append("/");
        jasminCodeBuilder.append(instruction.getSecondOperand().toString()).append(" ");
        jasminCodeBuilder.append(JasminUtils.typeCode(instruction.getSecondOperand().getType()));

        return jasminCodeBuilder.toString();
    }

    private String getGetFieldJasminString(Method method, GetFieldInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstOperand()));

        jasminCodeBuilder.append("\n\t");

        jasminCodeBuilder.append("getfield ");
        jasminCodeBuilder.append(instruction.getFirstOperand().getClass().getName()).append("/");
        jasminCodeBuilder.append(instruction.getSecondOperand().toString()).append(" ");
        jasminCodeBuilder.append(JasminUtils.typeCode(instruction.getSecondOperand().getType()));

        return jasminCodeBuilder.toString();
    }

    private String getUnaryOperJasminString(Method method, UnaryOpInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getOperand()));
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append(JasminUtils.operationCode(instruction.getOperation()));

        if (instruction.getOperation().getOpType() == OperationType.NOTB) {
            jasminCodeBuilder.append(" 1");
        } else {
            jasminCodeBuilder.append("; Invalid unary operation");
        }

        return jasminCodeBuilder.toString();
    }

    private String getBinaryOperJasminString(Method method, BinaryOpInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        Element left = instruction.getLeftOperand();
        Element right = instruction.getRightOperand();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, left));
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append(JasminUtils.loadElement(method, right));

        return jasminCodeBuilder.toString();
    }

    private String getNOperJasminString(Method method, SingleOpInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        // TODO

        return jasminCodeBuilder.toString();
    }
}
