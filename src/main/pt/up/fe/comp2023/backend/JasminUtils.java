package pt.up.fe.comp2023.backend;

import org.specs.comp.ollir.*;

import java.util.Objects;

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

            jasminCodeBuilder.append("aload").append(JasminUtils.regCode(method.getVarTable().get(operand.getName()).getVirtualReg()));
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
                if(Objects.equals(((Operand) element).getName(), "this")) {
                    jasminCodeBuilder.append("aload_0");
                } else {
                    jasminCodeBuilder.append("aload").append(regCode(method.getVarTable().get(op.getName()).getVirtualReg()));
                }
            } else {
                throw new RuntimeException("loadElement not implemented for Operand " + element.getClass().toString() + ".");
            }
        } else {
            throw new RuntimeException("loadElement not implemented for " + element.getClass().toString() + ".");
        }

        return jasminCodeBuilder.toString();
    }

    public static String storeElement(Method method, Element element) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        Operand op = (Operand) element;

        if (op.getType().getTypeOfElement() == ElementType.INT32 ||
                op.getType().getTypeOfElement() == ElementType.BOOLEAN) {
            if (method.getVarTable().get(op.getName()).getVarType().getTypeOfElement() == ElementType.ARRAYREF){
                jasminCodeBuilder.append("iastore");
            } else {
                jasminCodeBuilder.append("istore").append(regCode(method.getVarTable().get(op.getName()).getVirtualReg()));
            }
        } else if (op.getType().getTypeOfElement() == ElementType.STRING ||
                op.getType().getTypeOfElement() == ElementType.OBJECTREF ||
                op.getType().getTypeOfElement() == ElementType.ARRAYREF ||
                op.getType().getTypeOfElement() == ElementType.THIS) {
            jasminCodeBuilder.append("astore").append(regCode(method.getVarTable().get(op.getName()).getVirtualReg()));
        } else {
            throw new RuntimeException("storeElement not implemented for Operand " + element.getClass().toString() + ".");
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

            case ANDB, AND -> jasminCodeBuilder.append("iand");
            case EQ -> jasminCodeBuilder.append("eq");
            case NEQ, NOTB -> jasminCodeBuilder.append("ne");
            case ORB -> jasminCodeBuilder.append("or");
            case LTH -> jasminCodeBuilder.append("lt");
            case GTH -> jasminCodeBuilder.append("gt");
            case LTE -> jasminCodeBuilder.append("le");
            case GTE -> jasminCodeBuilder.append("ge");

            default -> throw new UnsupportedOperationException("Operation not implemented: " + operation.getOpType());
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
            default -> throw new UnsupportedOperationException("Type not implemented: " + typeOfElement);
        }

        return jasminCodeBuilder.toString();
    }

    public static String booleanResult(int condition_label) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(" TRUE_" + condition_label);
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append("iconst_0");
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append("goto " + "CONTINUE_" + condition_label);
        jasminCodeBuilder.append("\n");
        jasminCodeBuilder.append("TRUE_" + condition_label + ":");
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append("iconst_1");
        jasminCodeBuilder.append("\n");
        jasminCodeBuilder.append("CONTINUE_" + condition_label + ":");

        return jasminCodeBuilder.toString();
    }

    public static boolean isConditionalOperation(Operation operation) {
        return switch (operation.getOpType()) {
            case ORB, LTH, GTH, LTE, GTE, EQ, NEQ -> true;
            default -> false;
        };
    }

    public static Operation inverseOperation(Operation operation) {
        return switch (operation.getOpType()) {
            case LTH -> new Operation(OperationType.GTH, operation.getTypeInfo());
            case GTH -> new Operation(OperationType.LTH, operation.getTypeInfo());
            case LTE -> new Operation(OperationType.GTE, operation.getTypeInfo());
            case GTE -> new Operation(OperationType.LTE, operation.getTypeInfo());
            default -> operation;
        };
    }
}
