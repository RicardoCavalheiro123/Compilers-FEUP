package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.instructions.call.*;


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
                jasminCodeBuilder.append("\t.limit stack ").append(this.stackMaxSize + 2).append("\n");
                jasminCodeBuilder.append("\t.limit locals ").append(getLimitLocal(method)).append("\n");
            }

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

        method.getLabels(instruction).forEach(label -> jasminCodeBuilder.append("\n").append(label).append(":\n\t"));
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
            updateStack(1);
            jasminCodeBuilder.append(JasminUtils.loadElement(method, aop.getIndexOperands().get(0))).append("\n\t");
            updateStack(1);

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
        updateStack(-1);

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
        if(!method.isConstructMethod()) updateStack(callInstruction.getStackChange());

        if(!method.isConstructMethod() && !this.isAssign && ((callInstruction instanceof InvokeSpecialInstruction) || (instruction.getReturnType().getTypeOfElement() != ElementType.VOID))) {
            jasminCodeBuilder.append("\n\t");
            jasminCodeBuilder.append("pop");
            updateStack(-1);
        }

        return jasminCodeBuilder.toString();
    }

    private String getGotoJasminString(Method method, GotoInstruction instruction) {
        return "goto " + instruction.getLabel();
    }

    private String getBranchJasminString(Method method, CondBranchInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();
        InstructionType conditionType = instruction.getCondition().getInstType();
        boolean hasZero = false;
        if (conditionType == InstructionType.BINARYOPER) {
            BinaryOpInstruction binaryOpInstruction = (BinaryOpInstruction) instruction.getCondition();
            if(!(binaryOpInstruction.getLeftOperand().isLiteral() && ((LiteralElement) binaryOpInstruction.getLeftOperand()).getLiteral().equals("0"))) {
                jasminCodeBuilder.append(JasminUtils.loadElement(method, binaryOpInstruction.getLeftOperand()));
                updateStack(1);
            } else {
                hasZero = true;
            }
            jasminCodeBuilder.append("\n\t");
            if(!(binaryOpInstruction.getRightOperand().isLiteral() && ((LiteralElement) binaryOpInstruction.getRightOperand()).getLiteral().equals("0"))) {
                jasminCodeBuilder.append(JasminUtils.loadElement(method, binaryOpInstruction.getRightOperand()));
                updateStack(1);
            } else {
                hasZero = true;
            }
        } else if (conditionType == InstructionType.NOPER) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getOperands().get(0)));
            updateStack(1);
        }
        jasminCodeBuilder.append("\n\t");

        String op;
        if (conditionType ==  InstructionType.BINARYOPER && !hasZero) {
            op = "if_icmp";
            op += JasminUtils.operationCode(((BinaryOpInstruction) instruction.getCondition()).getOperation());
            updateStack(-2);
        }
        else {
            op = "if" + JasminUtils.operationCode(new Operation(OperationType.NEQ, new Type(ElementType.BOOLEAN)));
            updateStack(-1);
        }

        jasminCodeBuilder.append(op).append(" ").append(instruction.getLabel());

        return jasminCodeBuilder.toString();
    }

    private String getReturnJasminString(Method method, ReturnInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        if(instruction.hasReturnValue()) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getOperand()));
            updateStack(1);
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
        updateStack(-1);

        return jasminCodeBuilder.toString();
    }

    private String getPutFieldJasminString(Method method, PutFieldInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstOperand()));
        updateStack(1);
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getThirdOperand()));
        updateStack(1);
        jasminCodeBuilder.append("\n\t");

        jasminCodeBuilder.append("putfield ");
        jasminCodeBuilder.append(((ClassType) instruction.getFirstOperand().getType()).getName()).append("/");
        jasminCodeBuilder.append(((Operand) instruction.getSecondOperand()).getName()).append(" ");
        jasminCodeBuilder.append(JasminUtils.typeCode(instruction.getSecondOperand().getType()));
        updateStack(-2);
        return jasminCodeBuilder.toString();
    }

    private String getGetFieldJasminString(Method method, GetFieldInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstOperand()));
        updateStack(1);
        jasminCodeBuilder.append("\n\t");

        jasminCodeBuilder.append("getfield ");
        jasminCodeBuilder.append(((ClassType) instruction.getFirstOperand().getType()).getName()).append("/");
        jasminCodeBuilder.append(((Operand) instruction.getSecondOperand()).getName()).append(" ");
        jasminCodeBuilder.append(JasminUtils.typeCode(instruction.getSecondOperand().getType()));
        updateStack(-1);
        return jasminCodeBuilder.toString();
    }

    private String getUnaryOperJasminString(Method method, UnaryOpInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        if (instruction.getOperation().getOpType() == OperationType.NOTB) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getOperand()));
            updateStack(1);
            jasminCodeBuilder.append("\n\t");

            jasminCodeBuilder.append("ifeq ");
            updateStack(-1);
            jasminCodeBuilder.append(JasminUtils.booleanResult(conditionCounter));
            conditionCounter++;
            updateStack(2);
        } else {
            throw new RuntimeException("Unary operation not supported");
        }

        return jasminCodeBuilder.toString();
    }

    private String getBinaryOperJasminString(Method method, BinaryOpInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        Element left = instruction.getLeftOperand();
        Element right = instruction.getRightOperand();
        boolean rightIsLiteral = right.isLiteral();
        boolean leftIsLiteral = left.isLiteral();
        boolean rightIsZero = rightIsLiteral && ((LiteralElement) right).getLiteral().equals("0");
        boolean leftIsZero = leftIsLiteral && ((LiteralElement) left).getLiteral().equals("0");
        boolean isConditional = JasminUtils.isConditionalOperation(instruction.getOperation());
        Operation operation = instruction.getOperation();

        if(isConditional && leftIsZero) {
            jasminCodeBuilder.append("iconst_0");
            jasminCodeBuilder.append("\n\t");
        }

        if (!(leftIsZero && isConditional)) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, left));
            updateStack(1);
            jasminCodeBuilder.append("\n\t");
        }

        if (!(rightIsZero && isConditional)) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, right));
            updateStack(1);
            jasminCodeBuilder.append("\n\t");
        }

        if(isConditional) {
            if (leftIsZero) {
                jasminCodeBuilder.append("isub");
                jasminCodeBuilder.append("\n\t");
                updateStack(-1);
            }
            if (leftIsZero || rightIsZero) {
                jasminCodeBuilder.append("if");
                updateStack(-1);
            }
            else {
                jasminCodeBuilder.append("if_icmp");
                updateStack(-2);
            }
        }

        jasminCodeBuilder.append(JasminUtils.operationCode(operation));

        if (isConditional) {
            jasminCodeBuilder.append(JasminUtils.booleanResult(conditionCounter));
            conditionCounter++;
            updateStack(2);
        }

        return jasminCodeBuilder.toString();
    }

    private String getNOperJasminString(Method method, SingleOpInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getSingleOperand()));
        updateStack(1);

        return jasminCodeBuilder.toString();
    }

    private int getLimitLocal(Method method) {
        final int[] maximum = {0};
        method.getVarTable().forEach((k, v) -> {
            if (v.getVirtualReg() > maximum[0]) maximum[0] = v.getVirtualReg();
        });
        return maximum[0] + 1;
    }

    public int updateStack(int stackUpdate) {
        this.stackSize += stackUpdate;
        if (this.stackSize > this.stackMaxSize)
            this.stackMaxSize = this.stackSize;
        return this.stackSize;
    }

}
