package net.geertvos.gvm.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
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

	//@Test()
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

	@Test()
	public void testArrays() throws IOException {
		URL url = Resources.getResource("arrayTest.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		compileAndRun(source);
	}

	public void testNativeObject() throws IOException {
		URL url = Resources.getResource("NativeObject.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		compileAndRun(source);
	}

	public static void write(String string, OutputStream stream) {
		try {
			stream.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static String getLineFeed() throws IOException {
		return "\r\n";
	}
	
	private void compileAndRun(String source) throws IOException {
		List<Module> parsedModules = new LinkedList<>();
		Module program = (Module) parse(source);
		Set<String> loadedModules = new HashSet<>();
		Queue<String> modulesToLoad = new LinkedList<String>();
		modulesToLoad.addAll(program.getImports());
		while(!modulesToLoad.isEmpty()) {
			String module = modulesToLoad.poll();
			if(!loadedModules.contains(module)) {
				URL url = Resources.getResource(module+".gs");
				String moduleSource = Resources.toString(url, StandardCharsets.UTF_8);
				loadedModules.add(module);
				Module loadedModule = (Module) parse(moduleSource);
				parsedModules.add(0, loadedModule);
				modulesToLoad.addAll(loadedModule.getImports());
			}
		}
		
		//Add the main program last
		parsedModules.add(program);
		GScriptCompiler compiler = new GScriptCompiler();
		GVMProgram p = compiler.compileModules(parsedModules);
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
