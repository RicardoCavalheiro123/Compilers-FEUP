package pt.up.fe.comp.cp2;

import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

public class SemanticAnalysisTest2 {

    @Test
    public void PrintOtherClassInline() {

        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/calls/PrintOtherClassInline.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void Inherited_Method_Call_Simple() {

        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/calls/Inherited_Method_Call_Simple.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void methodCallMissing() {

        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/calls/Method_Call_Missing.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void varNotDeclared() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/semanticanalysis/VarNotDeclared.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void Other_Class_Method_Call_Simple() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/calls/Other_Class_Method_Call_Simple.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void ImportSuper() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/import/ImportSuper.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void VarLookup_Field() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/lookup/VarLookup_Field.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void VarLookup_Field_Main_Fail() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/lookup/VarLookup_Field_Main_Fail.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void VarLookup_Local() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/lookup/VarLookup_Local.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void MethodsAndFields() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/symboltable/MethodsAndFields.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void Parameters() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/symboltable/Parameters.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void ThisAsArg() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/this/ThisAsArg.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void ThisInMain() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/this/ThisInMain.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void Array_Index() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/Array_Index.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void Array_Index_Bad() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/Array_Index_Bad.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void Array_Sum_Bad() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/Array_Sum_Bad.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void Assignment_Array_Bad() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/Assignment_Array_Bad.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void Assignment_Bool_Bad() {
        var result = TestUtils.analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/Assignment_Bool_Bad.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void BinaryOps_BadInts() {
        var result = TestUtils
                .analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/BinaryOps_BadInts.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void BinaryOps_Simple() {
        var result = TestUtils
                .analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/BinaryOps_Simple.jmm"));
        TestUtils.noErrors(result);
    }

    @Test
    public void While_If_Array_AccessArrayNotInteger() {
        var result = TestUtils
                .analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/While_If_Array_AccessArrayNotInteger.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void While_If_Array_IfCondNotBool() {
        var result = TestUtils
                .analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/While_If_Array_IfCondNotBool.jmm"));
        TestUtils.mustFail(result);
    }

    @Test
    public void While_If_Array_WhileCondBool() {
        var result = TestUtils
                .analyse(SpecsIo.getResource("pt/up/fe/comp/cp2/fixtures/public/cpf/2_semantic_analysis/type_verification/While_If_Array_WhileCondBool.jmm"));
        TestUtils.noErrors(result);
    }
}
