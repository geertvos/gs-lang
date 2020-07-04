package net.geertvos.gvm.lang.bridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.geertvos.gvm.bridge.NativeMethodWrapper;
import net.geertvos.gvm.bridge.ValueConverter;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.lang.GVMPlainObject;
import net.geertvos.gvm.program.GVMContext;

public class NativeObjectMethodWrapper extends NativeMethodWrapper {

	private Object parent;
	private String methodName;
	private int paramCount;
	
	public NativeObjectMethodWrapper(String methodName, Object parent, int paramCount) {
		this.parent = parent;
		this.methodName = methodName;
		this.paramCount = paramCount;
	}

	@Override
	public Value invoke(List<Value> arguments, GVMContext context) {
		ValueConverter converter = context.getProgram().getConverter();
		try {
			Object[] wrappedArgs = new Object[arguments.size()];
			Class<?>[] wrappedTypes = new Class[arguments.size()];
			for(int i=0;i<arguments.size();i++) {
				Object converted = converter.convertFromGVM(context, arguments.get(i));
				wrappedArgs[i] = converted;
				wrappedTypes[i] = converted.getClass();
			}

			Method theMethod = null;
			int count = 0;
			for(Method m : parent.getClass().getMethods()) {
				if(m.getName().equals(methodName)) {
					theMethod = m;
					count++;
				}
			}
			if(count > 1) {
				//Check arguments
				theMethod = parent.getClass().getMethod(methodName, wrappedTypes);
			}
			theMethod.setAccessible(true);
			for(int p=0;p<wrappedTypes.length;p++) {
				if(wrappedTypes[p] == GVMPlainObject.class ) {
					wrappedArgs[p] = converter.convertFromGVM(context, arguments.get(p), theMethod.getParameterTypes()[p]);
				}
			}
			Object returnValue = theMethod.invoke(parent, wrappedArgs);
			return converter.convertToGVM(context, returnValue);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int argumentCount() {
		return paramCount;
	}
	
}
