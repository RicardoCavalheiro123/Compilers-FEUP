package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;
import pt.up.fe.comp2023.backend.instructions.call.InstructionCall;

public class InvokeVirtualInstruction implements InstructionCall {
    int stackChange = 0;
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstArg()));

        for (Element element : instruction.getListOfOperands()) jasminCodeBuilder.append("\n\t").append(JasminUtils.loadElement(method, element));

        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append("invokevirtual ");

        jasminCodeBuilder.append(((ClassType) instruction.getFirstArg().getType()).getName().replace(".", "/"));
        jasminCodeBuilder.append("/" + ((LiteralElement) instruction.getSecondArg()).getLiteral().replace("\\", "").replace("\"", "")).append("(");

        for (Element e: instruction.getListOfOperands()) {
            jasminCodeBuilder.append(JasminUtils.typeCode(e.getType()));
            stackChange--;
        }

        jasminCodeBuilder.append(")" + JasminUtils.typeCode(instruction.getReturnType()));

        return jasminCodeBuilder.toString();
    }

    @Override
    public int getStackChange(CallInstruction instruction) {
        return stackChange;
    }
}
