package net.geertvos.gvm.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import com.google.common.io.Resources;

import net.geertvos.gvm.ast.Program;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.program.GVMProgram;

public class GVMIntegrationTest {

	private static boolean flag = false;
	
	@Test()
	public void testNativeFunctionCall() {
		String assignment = "native(\"net.geertvos.gvm.parser.GVMIntegrationTest\",\"setBoolean\");";
		compileAndRun(assignment);
		assertTrue(flag);
	}

	@Test()
	public void testLogic() throws IOException {
		URL url = Resources.getResource("logic-test.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		compileAndRun(source);
	}

	@Test()
	public void testScope() throws IOException {
		URL url = Resources.getResource("scope-test.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		compileAndRun(source);
	}

	@Test()
	public void testHashSet() throws IOException {
		URL url = Resources.getResource("hashset-test.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		compileAndRun(source);
	}

	
	private void compileAndRun(String source) {
		Program program = (Program) parse(source);
		GScriptCompiler compiler = new GScriptCompiler();
		GVMProgram p = compiler.compile(program.getAll());
		GVM vm = new GVM(p);
		vm.run();
	}

	public static void setBoolean() {
		flag = true;
	}
	

	public static void testEqualsInt(Integer value, Integer expected) {
		assertEquals((int)expected, (int)value);
	}

	public static void testEqualsBoolean(Boolean value,  Boolean expected) {
		assertEquals((boolean)expected, (boolean)value);
	}
	
	public static void testEqualsString(String value, String expected) {
		assertEquals(expected, value);
	}

	public static void print(String message) {
		System.out.println(message);
	}
	
	public Object parse(String code) {
		Parser parser = Parboiled.createParser(Parser.class);
		ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Program()).run(code);
		if (!result.parseErrors.isEmpty()) {
			System.out.println(ErrorUtils.printParseError(result.parseErrors.get(0)));
			Assert.fail(ErrorUtils.printParseError(result.parseErrors.get(0)));
		} else {
			String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
			System.out.println(parseTreePrintOut);
		}
		Object value = result.parseTreeRoot.getValue();
		return value;
	}

	
}
