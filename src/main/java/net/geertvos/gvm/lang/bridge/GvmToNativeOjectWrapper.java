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

/**
 * This class generates a Java Proxy that calls in to the GVM to execute a function.
 * 
 * @author Geert Vos
 *
 */
public class GvmToNativeOjectWrapper implements InvocationHandler {
 
	private final Value value;
	private final GVMContext context;
	
	public GvmToNativeOjectWrapper(GVMContext context, Value value) {
		this.context = context;
		this.value = value;
    }

	@Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		ValueConverter converter = context.getProgram().getConverter();
		//Generate wrapper function
		int argumentCount = 0;
		if(args != null) {
			argumentCount = args.length;
		}
		List<String> paramNames = new LinkedList<String>();
		for(Parameter p : method.getParameters()) {
			paramNames.add(p.getName());
		}
		//Generate a function to call the native method
		//Run in a fork of the original thread to copy existing scope
		GVMThread thread = context.getThread().fork();

		RandomAccessByteStream code = generateFunction(method, argumentCount);
		GVMFunction function = new GVMFunction(code, paramNames);
		int functionPointer = context.getProgram().addFunction(function);
		thread.setFunctionPointer(functionPointer);
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
		//TODO: disable GC
		//Blocks until finished
		boolean running = true;
		while(running) {
			running = context.getGVM().fetchAndDecode(thread);
		}
		context.getProgram().deleteFunction(functionPointer);
		//TODO: enable GC
        Value returnVal = thread.getStack().pop(); //TODO implement return values
        return converter.convertFromGVM(context, returnVal);
        
    }

	private RandomAccessByteStream generateFunction(Method method, int argumentCount) {
		RandomAccessByteStream code = new RandomAccessByteStream(256);
		int ref = context.getProgram().addString(method.getName());
		code.add(GVM.LDC_D);
		code.writeInt(ref);
		code.writeString(new StringType().getName());
		code.add(GVM.GET);
		code.add(GVM.INVOKE);
		code.writeInt(argumentCount);
		code.add(GVM.HALT);
		return code;
	}
}