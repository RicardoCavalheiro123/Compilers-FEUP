package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;

public class NewInstruction implements InstructionCall {
    int stackChange = 0;

    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        ElementType newType = instruction.getReturnType().getTypeOfElement();

        if (newType == ElementType.ARRAYREF) {

            for (Element e : instruction.getListOfOperands()) {
                jasminCodeBuilder.append(JasminUtils.loadElement(method, e)).append("\n\t");
            }

            if (instruction.getListOfOperands().get(0).getType().getTypeOfElement() == ElementType.INT32) {
                jasminCodeBuilder.append("newarray int");
            } else {
                throw new RuntimeException("New Array type not implemented");
            }

        } else if (newType == ElementType.OBJECTREF) {

            for (Element e : instruction.getListOfOperands()) {
                jasminCodeBuilder.append(JasminUtils.loadElement(method, e)).append("\n\t");
            }

            jasminCodeBuilder.append("new ")
                    .append(((Operand) instruction.getFirstArg()).getName())
                    .append("\n");
            stackChange++;
            jasminCodeBuilder.append("\tdup");
            stackChange++;

        } else {
            throw new RuntimeException("New type not implemented");
        }

        return jasminCodeBuilder.toString();
    }

    @Override
    public int getStackChange(CallInstruction instruction) {
        return stackChange;
    }
}
