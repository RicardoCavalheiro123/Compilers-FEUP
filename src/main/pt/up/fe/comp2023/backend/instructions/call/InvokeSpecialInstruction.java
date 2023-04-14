package pt.up.fe.comp2023.backend.instructions.call;

import org.specs.comp.ollir.*;
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
            jasminCode += ((ClassType) instruction.getFirstArg().getType()).getName().replace(".", "/");
        }

        jasminCode += "/<init>(";

        for (Element e: instruction.getListOfOperands()) {
            jasminCode += JasminUtils.typeCode(e.getType());
        }

        jasminCode += ")" + JasminUtils.typeCode(instruction.getReturnType());

        return jasminCode;
    }
}
