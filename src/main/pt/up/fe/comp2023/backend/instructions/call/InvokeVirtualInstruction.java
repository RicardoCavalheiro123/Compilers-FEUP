package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Method;
import pt.up.fe.comp2023.backend.instructions.call.InstructionCall;

public class InvokeVirtualInstruction implements InstructionCall {
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        // TODO

        return jasminCodeBuilder.toString();
    }
}
