package net.geertvos.gvm.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import com.google.common.io.Resources;

import net.geertvos.gvm.ast.Module;
import net.geertvos.gvm.ast.Statement;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.program.GVMProgram;

public class GVMIntegrationTest {

	private static boolean flag = false;
	
	@Test()
	public void testNativeFunctionCall() throws IOException {
		String assignment = "native(\"net.geertvos.gvm.parser.GVMIntegrationTest\",\"setBoolean\");";
		compileAndRunStatement(assignment);
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

	@Test()
	public void testImport() throws IOException {
		URL url = Resources.getResource("ImportTest.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		compileAndRun(source);
	}

	
	private void compileAndRun(String source) throws IOException {
		List<Module> modules = new LinkedList<>();
		Module program = (Module) parse(source);
		for(String module : program.getImports() ) {
			URL url = Resources.getResource(module+".gs");
			String moduleSource = Resources.toString(url, StandardCharsets.UTF_8);
			Module loadedModule = (Module) parse(moduleSource);
			modules.add(0, loadedModule);
		}
		
		//Add the main program last
		modules.add(program);
		GScriptCompiler compiler = new GScriptCompiler();
		GVMProgram p = compiler.compileModules(modules);
		GVM vm = new GVM(p);
		vm.run();
	}

	private void compileAndRunStatement(String source) throws IOException {
		Statement statement = (Statement) parseStatement(source);
		GScriptCompiler compiler = new GScriptCompiler();
		List<Statement> compilables = new LinkedList<Statement>();
		compilables.add(statement);
		GVMProgram p = compiler.compile(compilables);
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
			//String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
			//System.out.println(parseTreePrintOut);
		}
		Object value = result.parseTreeRoot.getValue();
		return value;
	}

	public Object parseStatement(String code) {
		Parser parser = Parboiled.createParser(Parser.class);
		ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Statement()).run(code);
		if (!result.parseErrors.isEmpty()) {
			System.out.println(ErrorUtils.printParseError(result.parseErrors.get(0)));
			Assert.fail(ErrorUtils.printParseError(result.parseErrors.get(0)));
//		} else {
//			String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
//			System.out.println(parseTreePrintOut);
		}
		Object value = result.parseTreeRoot.getValue();
		return value;
	}

	
}
