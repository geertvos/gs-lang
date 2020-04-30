package net.geertvos.gvm.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.geertvos.gvm.ast.LoopStatement;
import net.geertvos.gvm.ast.Statement;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.program.GVMFunction;
import net.geertvos.gvm.program.GVMProgram;
import net.geertvos.gvm.program.NativeMethodWrapper;
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
	
	public RandomAccessByteStream code;
	private GVMFunction function;
	private GVMProgram program;
	
	public GVMProgram compile(List<Statement> compilables)
	{
		program = new GVMProgram("demo");
		code = new RandomAccessByteStream();
		function = new GVMFunction(code, new ArrayList<String>());
		program.addFunction(function);
		
		code.add(GVM.NEW); //Init main function
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
