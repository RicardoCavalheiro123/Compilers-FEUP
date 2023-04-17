package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;

public class InvokeStaticInstruction implements InstructionCall {
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (Element e: instruction.getListOfOperands()) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, e));
        }

        jasminCodeBuilder.append("invokestatic ");

        jasminCodeBuilder.append(((ClassType) instruction.getFirstArg().getType()).getName().replace(".", "/"));
        jasminCodeBuilder.append("/" + ((LiteralElement) instruction.getSecondArg()).getLiteral().replace("\\", "")).append("(");

        for (Element e: instruction.getListOfOperands()) {
            jasminCodeBuilder.append(JasminUtils.typeCode(e.getType()));
        }

        jasminCodeBuilder.append(")" + JasminUtils.typeCode(instruction.getReturnType()));

        return jasminCodeBuilder.toString();
    }
}
