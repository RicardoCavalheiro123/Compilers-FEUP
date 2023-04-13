package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.ElementType;
import org.specs.comp.ollir.Method;
import pt.up.fe.comp2023.backend.JasminUtils;


public class InvokeSpecialInstruction implements InstructionCall {
    String superClassName;

    public InvokeSpecialInstruction(String superClassName) {
        this.superClassName = superClassName;
    }
    @Override
    public String toJasmin(Method method, CallInstruction instruction) {
        String jasminCode = "";

        jasminCode += JasminUtils.loadElement(method, instruction.getFirstArg());
        jasminCode += "\n\t";

        if (method.isConstructMethod()  && instruction.getFirstArg().getType().getTypeOfElement() == ElementType.THIS) {
            jasminCode += "invokenonvirtual ";
        } else {
            jasminCode += "invokespecial ";
        }

        if (instruction.getFirstArg().getType().getTypeOfElement() == ElementType.THIS) {
            jasminCode += superClassName;
        } else {
            jasminCode += instruction.getFirstArg().getClass().getName().replace(".", "/") +
                    "/" + instruction.getFirstArg();
        }

        jasminCode += "/<init>(";

        for (Element e: instruction.getListOfOperands()) {
            jasminCode += JasminUtils.typeCode(e.getType());
        }

        jasminCode += ")" + JasminUtils.typeCode(instruction.getReturnType());

        return jasminCode;
    }
}
