package net.geertvos.gvm.lang.demo;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import com.google.common.io.Resources;

import net.geertvos.gvm.ast.Module;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.parser.Parser;
import net.geertvos.gvm.program.GVMProgram;

public class VirtualMachineDemo {

	public static void main(String[] args) throws IOException {
		URL url = Resources.getResource("VirtualMachine.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		GVMRuntime runtime = new GVMRuntime();
		GVM gvm = runtime.load(source);
		gvm.run();
		runtime.execute("System.print(\"Message is: \"+VirtualMachine.msg);", gvm);
	}

}
