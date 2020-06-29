package net.geertvos.gvm.lang.bridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.NativeMethodWrapper;
import net.geertvos.gvm.bridge.ValueConverter;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.program.GVMContext;

@SuppressWarnings("rawtypes")
public class NativeStaticMethodAutoWrapper extends NativeMethodWrapper{

	private final int args;
	
	public NativeStaticMethodAutoWrapper(int size) {
		this.args = size;
	}

	@Override
	public Value invoke(List<Value> arguments, GVMContext context) {
		Collections.reverse(arguments);
		ValueConverter converter = context.getProgram().getConverter();
		String classname = converter.convertFromGVM(context, arguments.get(0)).toString();
		String method = converter.convertFromGVM(context, arguments.get(1)).toString();
			try {
				Class theClass = Class.forName(classname);
				Object[] wrappedArgs = null;
				Class[] wrappedTypes = null;
				if(args>2) {
					int argcount = args-2;
					wrappedArgs = new Object[argcount];
					wrappedTypes = new Class[argcount];
					for(int i=2;i<args;i++) {
						Object converted = converter.convertFromGVM(context, arguments.get(i));
						wrappedArgs[i-2] = converted;
						wrappedTypes[i-2] = converted.getClass();
					}
				}
				if(isConstructor(method, theClass)) {
					Constructor c = getConstructor(theClass, wrappedTypes);
					Object returnValue = c.newInstance(wrappedArgs);
					return converter.convertToGVM(context, returnValue);
				} else {
					Method theMethod = getIfMethod(method, theClass, wrappedTypes);
					if(theMethod != null) {
						Object returnValue = theMethod.invoke(null, wrappedArgs );
						return converter.convertToGVM(context, returnValue);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			throw new IllegalStateException("Unable to invoke the native method: "+classname+"."+method);
	}
	
	private boolean isConstructor(String name, Class theClass) {
		if(theClass.getSimpleName().equals(name)) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private Constructor getConstructor(Class theClass, Class[] types) {
		Constructor constructor = null;
		try {
			constructor = theClass.getConstructor(types);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		if(constructor != null) {
			return constructor;
		}
		for(Constructor c : theClass.getConstructors()) {
			next:
			if(c.getParameterCount() == types.length) {
				int i=0;
				for(Parameter p : c.getParameters()) {
					if(!isCompatible(p.getType(), types[i])) break next;
					i++;
				}
				return c;
			}
		}
		return null;
	}
	
	private boolean isCompatible(Class potentialPrimitive, Class objectType) {
		if(potentialPrimitive == int.class && objectType == Integer.class) return true;
		if(potentialPrimitive == double.class && objectType == Double.class) return true;
		if(potentialPrimitive == boolean.class && objectType == Boolean.class) return true;
		if(potentialPrimitive.isAssignableFrom(objectType) || objectType.isAssignableFrom(potentialPrimitive)) return true;
		return false;
	}
	
	private Method getIfMethod(String methodName, Class theClass, Class[] types) throws NoSuchMethodException, SecurityException {
		Method theMethod = null;
		int count = 0;
		for(Method m : theClass.getMethods()) {
			if(m.getName().equals(methodName)) {
				theMethod = m;
				count++;
			}
		}
		if(count > 1) {
			theMethod = theClass.getMethod(methodName, types);
		}
		return theMethod;
	}
	
	@Override
	public int argumentCount() {
		return args;
	}

}
