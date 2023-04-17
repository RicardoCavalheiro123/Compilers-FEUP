package pt.up.fe.comp.cp2;

import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import utils.ProjectTestUtils;

import java.util.Collections;

public class Backend2Test {
    @Test
    public void testClass1() {

        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures2/public/ollir/myclass1.ollir");
    }

    @Test
    public void testClass2() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures2/public/ollir/myclass2.ollir");
    }

    @Test
    public void testClass3() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures2/public/ollir/myclass3.ollir");
    }

    @Test
    public void testClass4() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures2/public/ollir/myclass4.ollir");
    }

    @Test
    public void testFac() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures2/public/ollir/Fac.ollir");
    }

    public static void testOllirToJasmin(String resource, String expectedOutput) {
        SpecsCheck.checkArgument(resource.endsWith(".ollir"), () -> "Expected resource to end with .ollir: " + resource);

        // If AstToJasmin pipeline, change name of the resource and execute other test
        if (TestUtils.hasAstToJasminClass()) {

            // Rename resource
            var jmmResource = SpecsIo.removeExtension(resource) + ".jmm";

            // Test Jmm resource
            var result = TestUtils.backend(SpecsIo.getResource(jmmResource));
            ProjectTestUtils.runJasmin(result, expectedOutput);

            return;
        }

        var ollirResult = new OllirResult(SpecsIo.getResource(resource), Collections.emptyMap());

        var result = TestUtils.backend(ollirResult);

        ProjectTestUtils.runJasmin(result, null);
    }

    public static void testOllirToJasmin(String resource) {
        testOllirToJasmin(resource, null);
    }
}
