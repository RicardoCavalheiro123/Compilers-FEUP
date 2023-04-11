package pt.up.fe.comp2023.backend.instructions;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Instruction;
import org.specs.comp.ollir.Method;

import java.util.ArrayList;

public class CallToJasmin {


    public static String getVirtualCallJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    public static String getInterfaceCallJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    public static String getStaticCallJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    public static String getSpecialCallJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        // Deal with special call from OLLIR to Jasmin
        // Special call is a call to a constructor

        // Get the name of the method
        String methodName = method.getMethodName();

        // Get the arguments of the method being called
        ArrayList<Element> arguments = instruction.getListOfOperands();

        // Get the return type of the method being called
        String returnType = instruction.getReturnType().toString();

        // Get the number of arguments
        int numberOfArguments = arguments.size();

        return jasminCode;
    }

    public static String getNewJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    public static String getArrayLengthJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

    public static String getLdcJasminString(Method method, CallInstruction instruction) {
        String jasminCode = "";

        return jasminCode;
    }

}
