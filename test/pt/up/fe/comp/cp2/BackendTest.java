package pt.up.fe.comp.cp2;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import utils.ProjectTestUtils;

import java.util.Collections;

public class BackendTest {

    @Test
    public void testHelloWorld() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/HelloWorld.ollir");
    }

    @Test
    public void testSimple() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/Simple.ollir");
    }


    @Test
    public void testHillClimbing() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/HillClimbing.ollir");
    }

    @Test
    public void testClass1() {

        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/myclass1.ollir");
    }

    @Test
    public void testClass2() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/myclass2.ollir");
    }

    @Test
    public void testClass3() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/myclass3.ollir");
    }

    @Test
    public void testClass4() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/myclass4.ollir");
    }

    @Test
    public void testFac() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/Fac.ollir");
    }

    @Test
    public void testLazySort() {
        testOllirToJasmin("pt/up/fe/comp/cp2/fixtures/public/ollir/QuickSort.ollir");
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
