package net.geertvos.gvm.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import net.geertvos.gvm.ast.Program;
import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;
import nl.gvm.program.GVMProgram;

public class GVMIntegrationTest {

	private static boolean flag = false;
	
	@Test()
	public void testNativeFunctionCall() {
		String assignment = "native(\"net.geertvos.gvm.parser.GVMIntegrationTest\",\"setBoolean\");";
		Program program = (Program) parse(assignment);
		GCompiler compiler = new GCompiler();
		GVMProgram p = compiler.compile(program.getAll());
		GVM vm = new GVM(p);
		vm.run();
		assertTrue(flag);
	}

	@Test()
	public void testNativeFunctionCall2() {
		String assignment = "native(\"net.geertvos.gvm.parser.GVMIntegrationTest\",\"testNumber\",1+3, 4);";
		Program program = (Program) parse(assignment);
		GCompiler compiler = new GCompiler();
		GVMProgram p = compiler.compile(program.getAll());
		GVM vm = new GVM(p);
		vm.run();
	}

	public static void setBoolean() {
		flag = true;
	}
	
	public static void testNumber(Integer value, Integer expected) {
		assertEquals((int)expected, (int)value);
	}
	
	public Object parse(String code) {
		Parser parser = Parboiled.createParser(Parser.class);
		ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Program()).run(code);
		if (!result.parseErrors.isEmpty()) {
			System.out.println(ErrorUtils.printParseError(result.parseErrors.get(0)));
			Assert.fail(ErrorUtils.printParseError(result.parseErrors.get(0)));
		}
		Object value = result.parseTreeRoot.getValue();
		return value;
	}

	
}
