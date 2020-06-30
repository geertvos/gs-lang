package net.geertvos.gvm.lang.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.bridge.ValueConverter;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.GVMThread;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.lang.types.StringType;
import net.geertvos.gvm.program.GVMContext;
import net.geertvos.gvm.program.GVMFunction;
import net.geertvos.gvm.streams.RandomAccessByteStream;

public class GvmToNativeOjectWrapper implements InvocationHandler {
 
	private Value value;
	private ValueConverter converter;
	private final GVMContext context;
	
	public GvmToNativeOjectWrapper(GVMContext context, Value value) {
		this.context = context;
		this.value = value;
    	this.converter = context.getProgram().getConverter();
    }

	@Override
    public Object invoke(Object proxy, Method method, Object[] args) 
      throws Throwable {
		System.out.println("Proxy called on "+method.getName());
		
		//Generate wrapper function
		int argumentCount = 0;
		if(args != null) {
			argumentCount = args.length;
		}
		RandomAccessByteStream code = new RandomAccessByteStream(256);
		List<String> paramNames = new LinkedList<String>();
		for(Parameter p : method.getParameters()) {
			paramNames.add(p.getName());
		}
		//Generate a function to call the native method
		int ref = context.getProgram().addString(method.getName());
		code.add(GVM.LDC_D);
		code.writeInt(ref);
		code.writeString(new StringType().getName());
		code.add(GVM.GET);
		code.add(GVM.INVOKE);
		code.writeInt(argumentCount);
		code.add(GVM.HALT);
		GVMFunction function = new GVMFunction(code, paramNames);
		int functionPointer = context.getProgram().addFunction(function);
//TODO: Rewrite to reuse existing thread, so we can also use GETDYNAMIC to get variables outside the current scope.
//		GVMThread thread = context.getThread();
		GVMThread thread = new GVMThread(context.getProgram(), context.getHeap());
		thread.setFunctionPointer(functionPointer);
//		int callerFunction = thread.getFunctionPointer();
//		thread.getCallStack().push(new StackFrame(thread.getBytecode().getPointerPosition(), thread.getFramepointer(), callerFunction, thread.getDebugLineNumber(), thread.getLocation(), value));
//		thread.setFramepointer(thread.getStack().size()-1);
		thread.setBytecode(code);
		//Set the this
		thread.getStack().push(value);
		//Push args
		if(argumentCount > 0 ) {
			for(Object o : args) {
				Value v = converter.convertToGVM(context, o);
				thread.getStack().push(v);
			}
		}
		thread.getStack().push(value);
		code.seek(0);
		//TODO: Add debug info support.. line numbers etc
		//TODO: Is this the right way to call back in to the GVM? Or should we find the original calling thread?
		GVM gvm = new GVM(context.getProgram(), context.getHeap());
		boolean running = true;
		while(running) {
			running = gvm.fetchAndDecode(thread);
		}
		context.getProgram().deleteFunction(functionPointer);
        Value returnVal = thread.getStack().pop(); //TODO implement return values
        return converter.convertFromGVM(context, returnVal);
        
    }
}