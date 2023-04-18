package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;

public class NewInstruction implements InstructionCall {
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        ElementType newType = instruction.getReturnType().getTypeOfElement();

        if (newType == ElementType.ARRAYREF) {

            // TODO

        } else if (newType == ElementType.OBJECTREF) {

            for (Element e : instruction.getListOfOperands()) {
                jasminCodeBuilder.append(JasminUtils.loadElement(method, e)).append("\n\t");
            }

            jasminCodeBuilder.append("new ")
                    .append(((Operand) instruction.getFirstArg()).getName())
                    .append("\n");

            jasminCodeBuilder.append("\tdup");

        } else {
            jasminCodeBuilder.append("; New type not implement for ").append(newType).append("\n");
        }

        return jasminCodeBuilder.toString();
    }
}
