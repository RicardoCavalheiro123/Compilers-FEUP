package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;

public class InvokeStaticInstruction implements InstructionCall {
    int stackChange = 0;
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (Element e: instruction.getListOfOperands()) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, e)).append("\n\t");
        }

        jasminCodeBuilder.append("invokestatic ");

        jasminCodeBuilder.append(((Operand) instruction.getFirstArg()).getName().replace(".", "/"));
        jasminCodeBuilder.append("/" + ((LiteralElement) instruction.getSecondArg()).getLiteral().replace("\\", "").replace("\"", "")).append("(");

        for (Element e: instruction.getListOfOperands()) {
            jasminCodeBuilder.append(JasminUtils.typeCode(e.getType()));
            stackChange--;
        }

        jasminCodeBuilder.append(")" + JasminUtils.typeCode(instruction.getReturnType()));
        stackChange++;

        return jasminCodeBuilder.toString();
    }

    @Override
    public int getStackChange() {
        return stackChange;
    }
}
