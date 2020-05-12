package net.geertvos.gvm.compiler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import net.geertvos.gvm.ast.Module;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.parser.Parser;
import net.geertvos.gvm.program.GVMProgram;

public class GScript {

	 @SuppressWarnings("unchecked")
	    public static void disableAccessWarnings() {
	        try {
	            Class unsafeClass = Class.forName("sun.misc.Unsafe");
	            Field field = unsafeClass.getDeclaredField("theUnsafe");
	            field.setAccessible(true);
	            Object unsafe = field.get(null);

	            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
	            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

	            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
	            Field loggerField = loggerClass.getDeclaredField("logger");
	            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
	            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
	        } catch (Exception ignored) {
	        }
	    }
	
	public static void main(String[] args) throws IOException {
		disableAccessWarnings();
		
		String source = readContent(args[0]);
		List<Module> parsedModules = new LinkedList<>();
		Module program = (Module) parse(source);
		Set<String> loadedModules = new HashSet<>();
		Queue<String> modulesToLoad = new LinkedList<String>();
		modulesToLoad.addAll(program.getImports());
		while(!modulesToLoad.isEmpty()) {
			String module = modulesToLoad.poll();
			if(!loadedModules.contains(module)) {
				String moduleSource = readContent(module+".gs");
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


	private static String readContent(String filename) throws IOException {
		String source = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
		return source;
	}
	

	public static Object parse(String code) {
		Parser parser = Parboiled.createParser(Parser.class);
		ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Program()).run(code);
		if (!result.parseErrors.isEmpty()) {
			System.out.println(ErrorUtils.printParseError(result.parseErrors.get(0)));
		}
		Object value = result.parseTreeRoot.getValue();
		return value;
	}

}
