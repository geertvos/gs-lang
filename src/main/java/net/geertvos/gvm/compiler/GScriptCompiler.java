package net.geertvos.gvm.compiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.geertvos.gvm.ast.LoopStatement;
import net.geertvos.gvm.ast.Module;
import net.geertvos.gvm.ast.Statement;
import net.geertvos.gvm.bridge.NativeMethodWrapper;
import net.geertvos.gvm.core.BooleanType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.lang.GscriptExceptionHandler;
import net.geertvos.gvm.lang.GscriptValueConverter;
import net.geertvos.gvm.lang.types.ArrayType;
import net.geertvos.gvm.lang.types.NumberType;
import net.geertvos.gvm.lang.types.ObjectType;
import net.geertvos.gvm.lang.types.StringType;
import net.geertvos.gvm.program.GVMFunction;
import net.geertvos.gvm.program.GVMProgram;
import net.geertvos.gvm.streams.RandomAccessByteStream;

/**
 * The GCompiler compiles a list of Statements into a bytecode program
 * that can be executed by the GVM. 
 * 
 * @author geertvos
 */
public class GScriptCompiler {

	private final List<String> varNamesConstants = new ArrayList<>();
	private final List<NativeMethodWrapper> natives = new ArrayList<>();
	private final Stack<LoopStatement> loopStack = new Stack<>();
	private final Set<CompilerOptimizations> enabledOptimizations = new HashSet<>();
	
	public RandomAccessByteStream code;
	private GVMFunction function;
	private GVMProgram program;
	private String currentModuleName = "unknown";
	
	public GScriptCompiler() {
		enabledOptimizations.add(CompilerOptimizations.TAIL_RECURSION);
	}
	
	public GVMProgram compile(List<Statement> compilables)
	{
		program = prepareProgram();
		code = new RandomAccessByteStream();
		function = new GVMFunction(code, new ArrayList<String>());
		program.addFunction(function);
		
		code.add(GVM.NEW); //Init main function
		code.writeString(new ObjectType().getName());
		for( Compilable s : compilables )
		{
			s.compile(this);
		}
		code.add(GVM.HALT); //Make sure the VM stops
		
		//Prepare bytecode
		function.setBytecode(code);
		program.setNatives(natives);
		return program;
	}

	public GVMProgram compileModules(List<Module> modules)
	{
		program = prepareProgram();
		code = new RandomAccessByteStream();
		function = new GVMFunction(code, new ArrayList<String>());
		program.addFunction(function);
		
		code.add(GVM.NEW); //Init main function
		code.writeString(new ObjectType().getName());
		for(Module m : modules) {
			this.currentModuleName = m.getName();
			m.compile(this);
		}
		code.add(GVM.HALT); //Make sure the VM stops
		
		//Prepare bytecode
		function.setBytecode(code);
		program.setNatives(natives);
		return program;
	}

	private GVMProgram prepareProgram() {
		program = new GVMProgram("demo", new GscriptExceptionHandler(), new GscriptValueConverter());
		program.registerType(new ObjectType());
		program.registerType(new StringType());
		program.registerType(new NumberType());
		program.registerType(new BooleanType());
		program.registerType(new ArrayType());
		return program;
	}
	
	
	//TODO: Move to symbol table inside program
	public int registerVariable(String svariableName) {
		if(!varNamesConstants.contains(svariableName)) {
			varNamesConstants.add(svariableName);
		}
		return varNamesConstants.indexOf(svariableName);
	}
	
	public GVMFunction getFunction() {
		return function;
	}
	
	public void setFunction(GVMFunction function) {
		this.function = function;
	}
	
	public GVMProgram getProgram() {
		return program;
	}
	
	public String getCurrentModule() {
		return this.currentModuleName;
	}
	
	public void pushLoop(LoopStatement loop) {
		loopStack.push(loop);
	}
	
	public LoopStatement popLoop() {
		return loopStack.pop();
	}
	
	public LoopStatement peekLoop() {
		return loopStack.peek();
	}
	
	public boolean isDebugModeEnabled() {
		return true;
	}
	
	public boolean isEnabled(CompilerOptimizations optimization) {
		return enabledOptimizations.contains(optimization);
	}
	
	public void enableOptimization(CompilerOptimizations optimization) {
		enabledOptimizations.add(optimization);
	}
	
	public void disableOptimization(CompilerOptimizations optimization) {
		enabledOptimizations.remove(optimization);
	}
	
	public boolean hasNativeMethod(NativeMethodWrapper method) {
		return natives.contains(method);
	}
	
	public int getNativeMethodIndex(NativeMethodWrapper method) {
		if(!hasNativeMethod(method)) {
			natives.add(method);
		}
		return natives.indexOf(method);
	}
}
