package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Method;
import pt.up.fe.comp2023.backend.JasminUtils;

public class LdcInstruction implements InstructionCall {
    int stackChange = 0;

    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstArg()));
        stackChange++;

        return jasminCodeBuilder.toString();
    }

    @Override
    public int getStackChange(CallInstruction instruction) {
        return stackChange;
    }
}
