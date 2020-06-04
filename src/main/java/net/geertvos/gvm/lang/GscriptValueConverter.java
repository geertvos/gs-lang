package net.geertvos.gvm.lang;

import java.lang.reflect.Proxy;

import net.geertvos.gvm.bridge.ValueConverter;
import net.geertvos.gvm.core.BooleanType;
import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Type;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.lang.bridge.GvmToNativeOjectWrapper;
import net.geertvos.gvm.lang.bridge.NativeObjectWrapper;
import net.geertvos.gvm.lang.types.GscriptObjectType;
import net.geertvos.gvm.lang.types.NumberType;
import net.geertvos.gvm.lang.types.StringType;
import net.geertvos.gvm.program.GVMContext;

public class GscriptValueConverter implements ValueConverter {

	public GscriptValueConverter() {
	}
	
	public Object convertFromGVM(GVMContext context ,Value value) {
		Type type = value.getType();
		if(type instanceof Undefined) {
			return null;
		} else if(type instanceof StringType) {
			return context.getProgram().getString(value.getValue());
		} else if(type instanceof NumberType) {
			return value.getValue();
		} else if(type instanceof BooleanType) {
			return value.getValue() > 0;
		} else if(type instanceof GscriptObjectType) {
			int objectId = value.getValue();
			Object backingObject = context.getHeap().getObject(objectId);
			if(backingObject instanceof NativeObjectWrapper) {
				return ((NativeObjectWrapper)backingObject).getObject();
			}
			return backingObject;
		} else {
			throw new RuntimeException("Argument type "+type+" not supported.");
		}
	}

	public Object convertFromGVM(GVMContext context ,Value value, Class convertTo) {
		Type type = value.getType();
		if(type instanceof Undefined) {
			return null;
		} else if(type instanceof StringType && convertTo == String.class) {
			return context.getProgram().getString(value.getValue());
		} else if(type instanceof NumberType && (convertTo == Integer.class || convertTo == int.class)) {
			return value.getValue();
		} else if(type instanceof BooleanType && (convertTo == Boolean.class || convertTo == boolean.class)) {
			return value.getValue() > 0;
		} else if(type instanceof GscriptObjectType) {
			int objectId = value.getValue();
			Object backingObject = context.getHeap().getObject(objectId);
			if(backingObject instanceof NativeObjectWrapper) {
				Object backing = ((NativeObjectWrapper)backingObject).getObject();
				if(backing.getClass() == convertTo) {
					return backing;
				} else {
					throw new RuntimeException("Request type conversino to "+convertTo+" not supported, backing type is: "+backing.getClass());
				}
			}
			Object proxyInstance = createProxyObject(context, value, convertTo);
			return proxyInstance;
		} else {
			throw new RuntimeException("Argument type "+type+" not supported.");
		}
	}

	private Object createProxyObject(GVMContext context ,Value value, Class convertTo) {
		Object proxyInstance = Proxy.newProxyInstance(
				  convertTo.getClassLoader(), 
				  new Class[] { convertTo }, 
				  new GvmToNativeOjectWrapper(context, value));
		return proxyInstance;
	}
	

	
	public Value convertToGVM(GVMContext context ,Object returnValue) {
		if(returnValue == null) {
			return new Value(0,  new Undefined());
		}
		else if(returnValue instanceof String) {
			String strVal = (String)returnValue;
			int index = context.getProgram().addString(strVal);
			return new Value(index, new StringType());
		}
		else if(returnValue instanceof Integer) {
			return new Value(((Integer)returnValue), new NumberType());
		}
		else if(returnValue instanceof Boolean) {
			return new Value(((Boolean)returnValue)?1:0, new BooleanType());
		}
		else if(returnValue instanceof GVMObject) {
			int index = context.getHeap().addObject((GVMObject)returnValue);
			return new Value(index, new GscriptObjectType());
		} else {
			NativeObjectWrapper wrapper = new NativeObjectWrapper(returnValue, context);
			int index = context.getHeap().addObject(wrapper);
			return new Value(index, new GscriptObjectType());
		}

	}
	
}
