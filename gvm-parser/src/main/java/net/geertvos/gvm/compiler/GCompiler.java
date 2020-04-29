package net.geertvos.gvm.compiler;

import java.util.ArrayList;
import java.util.List;

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
public class GCompiler {

	private List<String> stringConstants = new ArrayList<>();
	private List<String> varNamesConstants = new ArrayList<>();
	private List<NativeMethodWrapper> natives = new ArrayList<>();

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

		program.setConstants( stringConstants );
		program.setNatives(natives);
		return program;
		
	}
	
	public int registerString(String stringConstant) {
		if(!stringConstants.contains(stringConstant)) {
			stringConstants.add(stringConstant);
		}
		return stringConstants.indexOf(stringConstant);
	}
	
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
