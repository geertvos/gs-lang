package net.geertvos.gvm.compiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import net.geertvos.gvm.bridge.NativeRegistry;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.lang.GscriptExceptionHandler;
import net.geertvos.gvm.lang.GscriptValueConverter;
import net.geertvos.gvm.lang.bridge.NativeStaticMethodAutoWrapper;
import net.geertvos.gvm.lang.types.ArrayType;
import net.geertvos.gvm.lang.types.NumberType;
import net.geertvos.gvm.lang.types.ObjectType;
import net.geertvos.gvm.lang.types.StringType;
import net.geertvos.gvm.debug.DebugInfo;
import net.geertvos.gvm.parser.Parser;
import net.geertvos.gvm.program.GVMProgram;
import net.geertvos.gvm.program.GVMProgramSerializer;
import net.geertvos.gvm.stdlib.StandardLibrary;

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
	
	private static NativeRegistry createRegistry() {
		NativeRegistry registry = new NativeRegistry();
		StandardLibrary.registerAll(registry);
		return registry;
	}

	public static void main(String[] args) throws IOException {
		disableAccessWarnings();

		List<String> allArgs = new ArrayList<>();
		boolean debug = false;
		boolean asm = false;
		for (String a : args) {
			if ("--debug".equals(a)) { debug = true; }
			else if ("--asm".equals(a)) { asm = true; }
			else { allArgs.add(a); }
		}

		if (allArgs.isEmpty()) {
			System.err.println("Usage:");
			System.err.println("  gs-lang <file.gs>                  Compile and run");
			System.err.println("  gs-lang --debug <file.gs>          Compile and run with debug tracing");
			System.err.println("  gs-lang --compile <file.gs> -o <file.gsc>  Compile to binary");
			System.err.println("  gs-lang --run <file.gsc>           Load and run binary");
			System.err.println("  gs-lang --debug --run <file.gsc>   Load and run binary with debug tracing");
			System.err.println("  gs-lang --asm <file.gs>            Compile and print assembly");
			System.err.println("  gs-lang --asm --run <file.gsc>     Load binary and print assembly");
			System.exit(1);
		}

		NativeRegistry registry = createRegistry();

		if ("--compile".equals(allArgs.get(0))) {
			if (allArgs.size() < 4 || !"-o".equals(allArgs.get(2))) {
				System.err.println("Usage: gs-lang --compile <file.gs> -o <file.gsc>");
				System.exit(1);
			}
			GVMProgram p = compileSource(Paths.get(allArgs.get(1)).toAbsolutePath(), registry);
			try (FileOutputStream out = new FileOutputStream(allArgs.get(3))) {
				GVMProgramSerializer.writeTo(p, out);
			}
			System.err.println("Compiled " + allArgs.get(1) + " -> " + allArgs.get(3));
		} else if ("--run".equals(allArgs.get(0))) {
			if (allArgs.size() < 2) {
				System.err.println("Usage: gs-lang --run <file.gsc>");
				System.exit(1);
			}
			GVMProgram p;
			try (FileInputStream in = new FileInputStream(allArgs.get(1))) {
				p = GVMProgramSerializer.readFrom(in, new GscriptExceptionHandler(), new GscriptValueConverter(),
						size -> new NativeStaticMethodAutoWrapper(size, registry));
			}
			registerRuntimeTypes(p);
			if (asm) {
				DebugInfo.disassemble(System.out, p);
			} else {
				GVM vm = new GVM(p);
				vm.run();
			}
		} else {
			String file = allArgs.get(0);
			if (file.endsWith(".gsc")) {
				GVMProgram p;
				try (FileInputStream in = new FileInputStream(file)) {
					p = GVMProgramSerializer.readFrom(in, new GscriptExceptionHandler(), new GscriptValueConverter(),
							size -> new NativeStaticMethodAutoWrapper(size, registry));
				}
				registerRuntimeTypes(p);
				if (asm) {
					DebugInfo.disassemble(System.out, p);
				} else {
					GVM vm = new GVM(p);
						vm.run();
				}
			} else {
				GVMProgram p = compileSource(Paths.get(file).toAbsolutePath(), registry);
				if (asm) {
					DebugInfo.disassemble(System.out, p);
				} else {
					GVM vm = new GVM(p);
						vm.run();
				}
			}
		}
	}

	private static Path findGslibDir(Path mainFile) {
		for (String var : new String[]{"GVM_HOME_JAVA", "GVM_HOME"}) {
			String home = System.getenv(var);
			if (home != null) {
				Path p = Paths.get(home, "gslib");
				if (Files.isDirectory(p)) {
					return p;
				}
			}
		}
		Path[] candidates = {
			mainFile.getParent().resolve("gslib"),
			Paths.get("gslib"),
			Paths.get(System.getProperty("user.dir"), "gslib")
		};
		for (Path p : candidates) {
			if (Files.isDirectory(p)) {
				return p;
			}
		}
		return null;
	}

	private static GVMProgram compileSource(Path main, NativeRegistry registry) throws IOException {
		String source = readContent(main);
		List<Module> parsedModules = new LinkedList<>();
		Module program = (Module) parse(source);
		Set<String> loadedModules = new HashSet<>();
		Queue<String> modulesToLoad = new LinkedList<String>();
		modulesToLoad.addAll(program.getImports());
		Path gslibDir = findGslibDir(main);
		while (!modulesToLoad.isEmpty()) {
			String module = modulesToLoad.poll();
			if (!loadedModules.contains(module)) {
				Path modulePath = Paths.get(main.getParent().toString() + "/" + module + ".gs");
				if (!Files.exists(modulePath) && gslibDir != null) {
					Path gslibPath = gslibDir.resolve(module + ".gs");
					if (Files.exists(gslibPath)) {
						modulePath = gslibPath;
					}
				}
				String moduleSource = readContent(modulePath);
				loadedModules.add(module);
				Module loadedModule = (Module) parse(moduleSource);
				parsedModules.add(0, loadedModule);
				modulesToLoad.addAll(loadedModule.getImports());
			}
		}
		parsedModules.add(program);
		GScriptCompiler compiler = new GScriptCompiler(registry);
		return compiler.compileModules(parsedModules);
	}

	private static void registerRuntimeTypes(GVMProgram p) {
		p.registerType(new ObjectType());
		p.registerType(new StringType());
		p.registerType(new NumberType());
		p.registerType(new ArrayType());
	}


	private static String readContent(Path file) throws IOException {
		String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
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
