package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.instructions.call.*;

import javax.management.Descriptor;
import java.util.HashSet;
import java.util.Set;

public class OllirToJasmin {
    String superClassName;
    boolean isAssign = false;
    int conditionCounter = 0;

    int stackSize = 0;
    int stackMaxSize = 0;

    public String getJasminString(ClassUnit resultOllirClass) {
        superClassName = resultOllirClass.getSuperClass() == null ? "java/lang/Object" : resultOllirClass.getSuperClass();

        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(getClassJasminString(resultOllirClass));
        jasminCodeBuilder.append(getMethodsJasminString(resultOllirClass));

        return jasminCodeBuilder.toString();
    }

    private String getClassJasminString(ClassUnit resultOllirClass) {
        return ".class public " + resultOllirClass.getClassName() + "\n" +
                ".super " + superClassName + "\n\t\n" +
                getFieldsJasminString(resultOllirClass) + "\t\n";
    }

    private String getFieldsJasminString(ClassUnit resultOllirClass) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (Field field : resultOllirClass.getFields()) {
            jasminCodeBuilder.append(".field ");

            if(field.getFieldAccessModifier() != AccessModifiers.DEFAULT) {
                jasminCodeBuilder.append(field.getFieldAccessModifier().toString().toLowerCase()).append(" ");
            }

            jasminCodeBuilder.append(field.getFieldName()).append(" ")
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
                jasminCodeBuilder.append(".method public ").append("<init>").append("(");

            } else {

                jasminCodeBuilder.append(".method ")
                        .append(method.getMethodAccessModifier().toString().toLowerCase())
                        .append(" ");

                if (method.isStaticMethod()) jasminCodeBuilder.append("static ");
                if (method.isFinalMethod()) jasminCodeBuilder.append("final ");

                jasminCodeBuilder.append(method.getMethodName()).append("(");
            }

            for (Element param : method.getParams()) {
                jasminCodeBuilder.append(JasminUtils.typeCode(param.getType()));
            }
            jasminCodeBuilder.append(")").append(JasminUtils.typeCode(method.getReturnType())).append("\n");

            StringBuilder instructions = new StringBuilder();
            for (Instruction instruction : method.getInstructions()) {
                instructions.append("\t").append(getInstructionJasminString(method, instruction)).append("\n");
            }

            if (method.getMethodAccessModifier() != AccessModifiers.DEFAULT) {
                jasminCodeBuilder.append("\t.limit stack ").append("99").append("\n");
                jasminCodeBuilder.append("\t.limit locals ").append("99").append("\n");
            }

            Set<Integer> hash_Set = new HashSet<Integer>();
            // HERE TODO

            jasminCodeBuilder.append(instructions);

            if (method.getMethodAccessModifier() == AccessModifiers.DEFAULT) {
                jasminCodeBuilder.append("\treturn\n");
            }

            jasminCodeBuilder.append(".end method\n\t\n");
            this.stackSize = 0;
            this.stackMaxSize = 0;
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

        Element dest = instruction.getDest();
        if(dest instanceof ArrayOperand) {
            ArrayOperand aop = ((ArrayOperand) dest);
            jasminCodeBuilder.append("aload").append(JasminUtils.regCode(method.getVarTable().get(aop.getName()).getVirtualReg())).append("\n\t");
            jasminCodeBuilder.append(JasminUtils.loadElement(method, aop.getIndexOperands().get(0))).append("\n\t");

        } else {
            if (instruction.getRhs().getInstType() == InstructionType.BINARYOPER) {
                BinaryOpInstruction binaryOpInstruction = (BinaryOpInstruction) instruction.getRhs();

                if (binaryOpInstruction.getOperation().getOpType() == OperationType.ADD || binaryOpInstruction.getOperation().getOpType() == OperationType.SUB) {
                    boolean ll = binaryOpInstruction.getLeftOperand().isLiteral();
                    boolean rl = binaryOpInstruction.getRightOperand().isLiteral();
                    LiteralElement literalElement = null;
                    Operand operand = null;

                    if (ll && !rl) {
                        literalElement = (LiteralElement) binaryOpInstruction.getLeftOperand();
                        operand = (Operand) binaryOpInstruction.getRightOperand();
                    } else if (!ll && rl) {
                        literalElement = (LiteralElement) binaryOpInstruction.getRightOperand();
                        operand = (Operand) binaryOpInstruction.getLeftOperand();
                    }
                    if (literalElement != null && operand != null) {
                         if (operand.getName().equals(((Operand) dest).getName())) {
                             int value = Integer.parseInt(literalElement.getLiteral());
                             if (binaryOpInstruction.getOperation().getOpType() == OperationType.SUB) value = -value;
                             if (value >= -128 && value <= 127)
                                return "iinc " + method.getVarTable().get(operand.getName()).getVirtualReg() + " " + value;
                        }
                    }
                }
            }
        }

        this.isAssign = true;
        jasminCodeBuilder.append(getInstructionJasminString(method, instruction.getRhs()));
        this.isAssign = false;
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append(JasminUtils.storeElement(method, dest));

        return jasminCodeBuilder.toString();
    }

    private String getCallJasminString(Method method, CallInstruction instruction) {
        InstructionCall callInstruction = null;
        StringBuilder jasminCodeBuilder = new StringBuilder();

        switch (instruction.getInvocationType()) {
            case invokevirtual -> callInstruction = new InvokeVirtualInstruction();
            case invokeinterface -> callInstruction = new InvokeInterfaceInstruction();
            case invokespecial -> callInstruction = new InvokeSpecialInstruction(superClassName);
            case invokestatic -> callInstruction = new InvokeStaticInstruction();
            case NEW -> callInstruction = new NewInstruction();
            case arraylength -> callInstruction = new ArrayLengthInstruction();
            case ldc -> callInstruction = new LdcInstruction();
        }

        jasminCodeBuilder.append(callInstruction.toJasmin(method, instruction));


        if(!method.isConstructMethod() && !this.isAssign && ((callInstruction instanceof InvokeSpecialInstruction) || (instruction.getReturnType().getTypeOfElement() != ElementType.VOID))) {
            jasminCodeBuilder.append("\n\t");
            jasminCodeBuilder.append("pop");
        }

        return jasminCodeBuilder.toString();
    }

    private String getGotoJasminString(Method method, GotoInstruction instruction) {
        return "goto " + instruction.getLabel();
    }

    private String getBranchJasminString(Method method, CondBranchInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getOperands().get(0)));

        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append("ifne ").append(instruction.getLabel());

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
        jasminCodeBuilder.append(((ClassType) instruction.getFirstOperand().getType()).getName()).append("/");
        jasminCodeBuilder.append(((Operand) instruction.getSecondOperand()).getName()).append(" ");
        jasminCodeBuilder.append(JasminUtils.typeCode(instruction.getSecondOperand().getType()));

        return jasminCodeBuilder.toString();
    }

    private String getGetFieldJasminString(Method method, GetFieldInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstOperand()));

        jasminCodeBuilder.append("\n\t");

        jasminCodeBuilder.append("getfield ");
        jasminCodeBuilder.append(((ClassType) instruction.getFirstOperand().getType()).getName()).append("/");
        jasminCodeBuilder.append(((Operand) instruction.getSecondOperand()).getName()).append(" ");
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
            throw new RuntimeException("Unary operation not supported");
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
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append(JasminUtils.operationCode(instruction.getOperation()));

        // If boolean operation
        if (JasminUtils.isConditionalOperation(instruction.getOperation())) {
            jasminCodeBuilder.append(JasminUtils.booleanResult(conditionCounter));
            conditionCounter++;
        }

        return jasminCodeBuilder.toString();
    }

    private String getNOperJasminString(Method method, SingleOpInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getSingleOperand()));

        return jasminCodeBuilder.toString();
    }

    public int updateStack(int stackUpdate) {
        this.stackSize += stackUpdate;
        if (this.stackSize > this.stackMaxSize)
            this.stackMaxSize = this.stackSize;
        if (this.stackSize < 0)
            throw new RuntimeException("Stack size is negative");
        return this.stackSize;
    }
}
