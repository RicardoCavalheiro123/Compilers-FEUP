package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;
import pt.up.fe.comp2023.backend.instructions.call.InstructionCall;

public class InvokeVirtualInstruction implements InstructionCall {
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstArg()));

        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append("invokevirtual ");

        return JasminUtils.invokes(instruction, jasminCodeBuilder);
    }
}
