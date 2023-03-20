package pt.up.fe.comp;
/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsStrings;

import java.util.HashMap;

public class BackendTest {

    // @Test
    // public void testHelloWorld() {
    // var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
    // TestUtils.noErrors(result.getReports());
    // var output = result.run();
    // assertEquals("Hello, World!", output.trim());
    // }

    @Test
    public void testHelloWorld() {

        String jasminCode = SpecsIo.getResource("pt/up/fe/comp/jasmin/HelloWorld.j");
        var output = TestUtils.runJasmin(jasminCode);
        assertEquals("Hello World!\nHello World Again!\n", SpecsStrings.normalizeFileContents(output));
    }

    @Test
    public void testJasmin() {

        TestUtils.backend(new OllirResult("myClass {\n" +
                "\t.construct myClass().V {\n" +
                "\t\tinvokespecial(this, \"<init>\").V;\n" +
                "\t}\n" +
                "\t\n" +
                "\t.method public check(A.array.classArray, b.Foo).bool {\n" +
                "\t\tall.bool :=.bool 0.bool;\n" +
                "\t\t\n" +
                "\t\tc.Foo :=.Foo $2.b.Foo;\n" +
                "\t\tinvokevirtual(c.Foo,\"test\",$1.A.array.classArray).V;\n" +
                "\n" +
                "\t\tb.bool :=.bool !.bool true.bool;\n" +
                "\n" +
                "\n" +
                "\t\tret.bool all.bool;\n" +
                "\t}\n" +
                "}", new HashMap<>())).run();

    }


}
