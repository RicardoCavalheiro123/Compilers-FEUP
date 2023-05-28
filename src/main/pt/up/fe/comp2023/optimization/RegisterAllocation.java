package pt.up.fe.comp2023.optimization;

import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterAllocation {

    public Integer allocation(ClassUnit ollirClass, Integer numberRegisters) {

        Map<Method, List<LiveNode>> liveAnalysis = new LiveAnalysis(ollirClass).analysis();



        return 0;
    }

}
