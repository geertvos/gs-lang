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
import net.geertvos.gvm.ast.ScopeStatement;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.FunctionType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.GVMThread;
import net.geertvos.gvm.lang.types.ObjectType;
import net.geertvos.gvm.parser.Parser;
import net.geertvos.gvm.program.GVMFunction;
import net.geertvos.gvm.program.GVMProgram;
import net.geertvos.gvm.streams.RandomAccessByteStream;

public class GVMRuntime {

	private GScriptCompiler compiler;
	private GVMProgram program;

	public GVMRuntime() {
		compiler = new GScriptCompiler();
	}

	public GVM load(String source) throws IOException {
		List<Module> parsedModules = new LinkedList<>();
		Module mainModule = (Module) parseProgram(source);
		Set<String> loadedModules = new HashSet<>();
		Queue<String> modulesToLoad = new LinkedList<String>();
		modulesToLoad.addAll(mainModule.getImports());
		while(!modulesToLoad.isEmpty()) {
			String module = modulesToLoad.poll();
			if(!loadedModules.contains(module)) {
				URL url = Resources.getResource(module+".gs");
				String moduleSource = Resources.toString(url, StandardCharsets.UTF_8);
				loadedModules.add(module);
				Module loadedModule = (Module) parseProgram(moduleSource);
				parsedModules.add(0, loadedModule);
				modulesToLoad.addAll(loadedModule.getImports());
			}
		}
		
		//Add the main program last
		parsedModules.add(mainModule);
		program = compiler.compileModules(parsedModules);
		GVM vm = new GVM(program);
		return vm;
	}
	
	public void execute(String sourceCode, GVM gvm) {
		ScopeStatement scope = (ScopeStatement) parseStatements(sourceCode);

		
		//Generate a function to call the native method
		//Run in a fork of the original thread to copy existing scope
		GVMThread thread = gvm.spawnThread();
		RandomAccessByteStream code = new RandomAccessByteStream(512);
		GVMFunction function = new GVMFunction(code, new LinkedList<String>());
		int functionPointer = program.addFunction(function);
		thread.setFunctionPointer(functionPointer);
		thread.setBytecode(code);
		compiler.setFunction(function);
		compiler.code = code;
		function.setBytecode(code);
		
		scope.compile(compiler);
		code.write( GVM.HALT );
		code.seek(0);
		
		RandomAccessByteStream loader = new RandomAccessByteStream(512);
		loader.write( GVM.LDC_D );
		loader.writeInt(0);
		loader.writeString(new ObjectType().getName());
		loader.write( GVM.LDC_D );
		loader.writeInt(functionPointer);
		loader.writeString(new FunctionType().getName());
		loader.write( GVM.INVOKE );
		loader.writeInt(0);
		loader.seek(0);

		thread.setBytecode(loader);
		
		boolean running = true;
		while(running) {
			running = gvm.fetchAndDecode(thread);
		}
		program.deleteFunction(functionPointer);
        //Value returnVal = thread.getStack().pop(); //TODO implement return values
        //return converter.convertFromGVM(context, returnVal);
	}
	

	public static Object parseProgram(String code) {
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

	public static Object parseStatements(String code) {
		Parser parser = Parboiled.createParser(Parser.class);
		ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.StatementsOnly()).run(code);
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
