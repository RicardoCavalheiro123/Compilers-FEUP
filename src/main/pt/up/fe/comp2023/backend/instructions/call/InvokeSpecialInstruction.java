package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Method;
import pt.up.fe.comp2023.backend.JasminUtils;


public class InvokeSpecialInstruction implements InstructionCall {
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        String jasminCode = "";

        jasminCode += JasminUtils.loadElement(method, instruction.getFirstArg());
        jasminCode += "\n\t";

        jasminCode += "invokespecial ";

        jasminCode += instruction.getFirstArg().getClass().getName().replace(".", "/") +
                "/" + instruction.getFirstArg();

        jasminCode += "/<init>(";

        for (Element e: instruction.getListOfOperands()) {
            jasminCode += JasminUtils.typeCode(e.getType());
        }

        jasminCode += ")V";

        return jasminCode;
    }
}
