package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.*;

public class JasminUtils {
    public static String loadElement(Method method, Element element) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        if (element instanceof LiteralElement literal) {

            if (literal.getType().getTypeOfElement() == ElementType.INT32 ||
                    literal.getType().getTypeOfElement() == ElementType.BOOLEAN) {

                int value = Integer.parseInt(literal.getLiteral());

                if (value >= -1 && value <= 5) {
                    jasminCodeBuilder.append("iconst_");
                } else if (value >= -128 && value <= 127) {
                    jasminCodeBuilder.append("bipush ");
                } else if (value >= -32768 && value <= 32767) {
                    jasminCodeBuilder.append("sipush ");
                } else {
                    jasminCodeBuilder.append("ldc ");
                }

                jasminCodeBuilder.append(value);

            } else {
                jasminCodeBuilder.append("ldc ").append(literal.getLiteral());
            }

        } else if (element instanceof ArrayOperand arrayOperand) {
            ArrayOperand operand = (ArrayOperand) element;

            jasminCodeBuilder.append("aload").append(JasminUtils.regCode(method.getVarTable().get(operand.getName()).getVirtualReg())).append("\n"); // load array (ref)
            jasminCodeBuilder.append("\n\t");
            jasminCodeBuilder.append(loadElement(method, arrayOperand.getIndexOperands().get(0)));
            jasminCodeBuilder.append("\n\t");
            jasminCodeBuilder.append("iaload");

        } else if (element instanceof Operand op) {

            if (op.getType().getTypeOfElement() == ElementType.INT32 ||
                    op.getType().getTypeOfElement() == ElementType.BOOLEAN) {
                jasminCodeBuilder.append("iload").append(regCode(method.getVarTable().get(op.getName()).getVirtualReg()));
            } else if (op.getType().getTypeOfElement() == ElementType.STRING ||
                    op.getType().getTypeOfElement() == ElementType.OBJECTREF ||
                    op.getType().getTypeOfElement() == ElementType.ARRAYREF ||
                    op.getType().getTypeOfElement() == ElementType.THIS) {
                jasminCodeBuilder.append("aload").append(regCode(method.getVarTable().get(op.getName()).getVirtualReg()));
            } else {
                jasminCodeBuilder.append("; loadElement not implemented for Operand ").append(element.getClass().toString()).append(".");
            }
        } else {
            jasminCodeBuilder.append("; loadElement not implemented for ").append(element.getClass().toString()).append(".");
        }

        return jasminCodeBuilder.toString();
    }

    public static String storeElement(Method method, Element element) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        Operand op = (Operand) element;

        if (op.getType().getTypeOfElement() == ElementType.INT32 ||
                op.getType().getTypeOfElement() == ElementType.BOOLEAN) {
            jasminCodeBuilder.append("istore").append(regCode(method.getVarTable().get(op.getName()).getVirtualReg()));
        } else if (op.getType().getTypeOfElement() == ElementType.STRING ||
                op.getType().getTypeOfElement() == ElementType.OBJECTREF ||
                op.getType().getTypeOfElement() == ElementType.ARRAYREF ||
                op.getType().getTypeOfElement() == ElementType.THIS) {
            jasminCodeBuilder.append("astore").append(regCode(method.getVarTable().get(op.getName()).getVirtualReg()));
        } else {
            jasminCodeBuilder.append("; storeElement not implemented for Operand ").append(element.getClass().toString()).append(".");
        }

        return jasminCodeBuilder.toString();
    }

    public static String regCode(int virtualReg) {
        if (virtualReg >= 0 && virtualReg <= 3) {
            return "_" + virtualReg;
        } else {
            return " " + virtualReg;
        }
    }

    public static String operationCode(Operation operation) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        switch (operation.getOpType()) {
            case ADD -> jasminCodeBuilder.append("iadd");
            case SUB -> jasminCodeBuilder.append("isub");
            case MUL -> jasminCodeBuilder.append("imul");
            case DIV -> jasminCodeBuilder.append("idiv");


            case ANDB -> jasminCodeBuilder.append("ifeq");
            case NOTB -> jasminCodeBuilder.append("ifne");
            case ORB -> jasminCodeBuilder.append("if_icmpor");
            case LTH -> jasminCodeBuilder.append("if_icmplt");
            case GTH -> jasminCodeBuilder.append("if_icmpgt");
            case LTE -> jasminCodeBuilder.append("if_icmple");
            case GTE -> jasminCodeBuilder.append("if_icmpge");

            default -> jasminCodeBuilder.append("; operationCode not implemented for ").append(operation.getOpType().toString()).append(".");
        }

        return jasminCodeBuilder.toString();
    }

    public static String typeCode(Type type) {
        StringBuilder jasminCodeBuilder = new StringBuilder();
        ElementType typeOfElement = type.getTypeOfElement();
        boolean array = false;

        while (typeOfElement == ElementType.ARRAYREF) {
            jasminCodeBuilder.append("[");
            typeOfElement = ((ArrayType) type).getArrayType();
            type = ((ArrayType) type).getElementType();
        }

        switch (typeOfElement) {
            case INT32 -> jasminCodeBuilder.append("I");
            case BOOLEAN -> jasminCodeBuilder.append("Z");
            case STRING -> jasminCodeBuilder.append("Ljava/lang/String;");
            case VOID -> jasminCodeBuilder.append("V");
            case OBJECTREF -> jasminCodeBuilder.append("L").append(((ClassType) type).getName().replace(".", "/")).append(";");
            default -> jasminCodeBuilder.append("; getTypeCode not implemented for ").append(type).append(".");
        }

        return jasminCodeBuilder.toString();
    }

    public static String className(Method method, String name) {
        return name.replace(".", "/");
    }
}
