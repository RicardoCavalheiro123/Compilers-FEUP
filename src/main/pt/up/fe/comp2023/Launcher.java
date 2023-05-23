package pt.up.fe.comp2023;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;

import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.parser.JmmParserResult;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

public class Launcher {

    public static void main(String[] args) {
        // Setups console logging and other things
        SpecsSystem.programStandardInit();

        // Parse arguments as a map with predefined options
        var config = parseArgs(args);

        // Get input file
        File inputFile = new File(config.get("inputFile"));

        // Check if file exists
        if (!inputFile.isFile()) {
            throw new RuntimeException("Expected a path to an existing input file, got '" + inputFile + "'.");
        }

        // Read contents of input file
        String code = SpecsIo.read(inputFile);

        // Instantiate JmmParser
        SimpleParser parser = new SimpleParser();

        // Parse stage
        JmmParserResult parserResult;
        parserResult = parser.parse(code, config);

        // Check if there are parsing errors
        TestUtils.noErrors(parserResult.getReports());

        System.out.println(parserResult.getRootNode().toTree());


        // Instantiate JmmAnalyzer
        SimpleAnalysis analyzer = new SimpleAnalysis();

        // Semantic analysis stage
        JmmSemanticsResult semanticsResult = analyzer.semanticAnalysis(parserResult);

        // Check if there are semantic errors
        TestUtils.noErrors(semanticsResult);

        System.out.println(semanticsResult.getSymbolTable());


        // Instantiate JmmOptimizer
        SimpleOptimization ollir = new SimpleOptimization();

        //Optimization before ollir
        if(config.get("optimize").equals("true")) {
            semanticsResult = ollir.optimize(semanticsResult);
        }

        TestUtils.noErrors(semanticsResult);

        // Ollir stage
        OllirResult ollirResult = ollir.toOllir(semanticsResult);

        // Check if there are ollir errors
        TestUtils.noErrors(ollirResult.getReports());

        System.out.println(ollirResult.getOllirCode());


        // Instantiate JasminGenerator
        SimpleBackend simpleBackend = new SimpleBackend();

        // Generate Jasmin code stage
        JasminResult jasminResult = simpleBackend.toJasmin(ollirResult);

        // Check if there are jasmin errors
        TestUtils.noErrors(jasminResult.getReports());

        jasminResult.run();

        System.out.println(jasminResult.getJasminCode());

        // ... add remaining stages
    }

    private static Map<String, String> parseArgs(String[] args) {
        SpecsLogs.info("Executing with args: " + Arrays.toString(args));

        // Check if there is at least one argument
        if (args.length != 1) {
            throw new RuntimeException("Expected a single argument, a path to an existing input file.");
        }

        // Create config
        Map<String, String> config = new HashMap<>();
        config.put("inputFile", args[0]);
        config.put("optimize", "false");
        config.put("registerAllocation", "-1");
        config.put("debug", "false");

        return config;
    }

}
