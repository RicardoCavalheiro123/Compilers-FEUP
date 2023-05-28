package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;


public class InvokeSpecialInstruction implements InstructionCall {
    String superClassName;
    int stackChange = 0;

    public InvokeSpecialInstruction(String superClassName) {
        this.superClassName = superClassName;
    }
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstArg()));
        stackChange++;
        jasminCodeBuilder.append("\n\t");

        if (method.isConstructMethod()  && instruction.getFirstArg().getType().getTypeOfElement() == ElementType.THIS) {
            jasminCodeBuilder.append("invokenonvirtual ");
        } else {
            jasminCodeBuilder.append("invokespecial ");
        }

        if (instruction.getFirstArg().getType().getTypeOfElement() == ElementType.THIS) {
            jasminCodeBuilder.append(superClassName);
        } else {
            jasminCodeBuilder.append(((ClassType) instruction.getFirstArg().getType()).getName().replace(".", "/"));
        }

        jasminCodeBuilder.append("/<init>(");

        for (Element e: instruction.getListOfOperands()) {
            jasminCodeBuilder.append(JasminUtils.typeCode(e.getType()));
            stackChange--;
        }

        jasminCodeBuilder.append(")" + JasminUtils.typeCode(instruction.getReturnType()));

        return jasminCodeBuilder.toString();
    }

    @Override
    public int getStackChange() {
        return stackChange;
    }
}
