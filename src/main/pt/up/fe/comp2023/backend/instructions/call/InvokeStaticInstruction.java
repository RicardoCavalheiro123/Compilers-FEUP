package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
import pt.up.fe.comp2023.backend.JasminUtils;

public class InvokeStaticInstruction implements InstructionCall {
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        StringBuilder jasminCodeBuilder = new StringBuilder();

        for (Element e: instruction.getListOfOperands()) {
            jasminCodeBuilder.append(JasminUtils.loadElement(method, e)).append("\n\t");
        }

        jasminCodeBuilder.append("invokestatic ");

        return JasminUtils.invokes(instruction, jasminCodeBuilder);
    }
}
