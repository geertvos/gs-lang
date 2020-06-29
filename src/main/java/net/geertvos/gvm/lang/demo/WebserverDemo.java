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

public class WebserverDemo {

	public static void main(String[] args) throws IOException {
		URL url = Resources.getResource("Webserver.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		compileAndRun(source);
	}
	
	private static void compileAndRun(String source) throws IOException {
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

	public static Object parse(String code) {
		Parser parser = Parboiled.createParser(Parser.class);
		ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Program()).run(code);
		if (!result.parseErrors.isEmpty()) {
			System.out.println(ErrorUtils.printParseError(result.parseErrors.get(0)));
		} else {
			//String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
			//System.out.println(parseTreePrintOut);
		}
		Object value = result.parseTreeRoot.getValue();
		return value;
	}

}
