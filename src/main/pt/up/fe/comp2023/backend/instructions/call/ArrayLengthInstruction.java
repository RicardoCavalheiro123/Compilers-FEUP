package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Method;
import pt.up.fe.comp2023.backend.JasminUtils;

public class ArrayLengthInstruction implements InstructionCall {
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        jasminCodeBuilder.append(JasminUtils.loadElement(method, instruction.getFirstArg()));
        jasminCodeBuilder.append("\n\t");
        jasminCodeBuilder.append("arraylength");

        return jasminCodeBuilder.toString();
    }
}
