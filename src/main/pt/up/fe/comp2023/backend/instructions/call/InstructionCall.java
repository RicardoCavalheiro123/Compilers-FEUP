package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Method;

public interface InstructionCall {
    public String toJasmin(Method method, CallInstruction instruction);
}