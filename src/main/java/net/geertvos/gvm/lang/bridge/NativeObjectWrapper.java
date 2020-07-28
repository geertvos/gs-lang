package net.geertvos.gvm.lang.bridge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.geertvos.gvm.bridge.ValueConverter;
import net.geertvos.gvm.core.FunctionType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.program.GVMContext;
import net.geertvos.gvm.program.GVMFunction;
import net.geertvos.gvm.streams.RandomAccessByteStream;

/**
 * WRaps Java object in a GScript object. Generating method to call in to the native method.
 * @author geert
 *
 */
public class NativeObjectWrapper implements GVMObject {

	private Object object;
	private Map<String,Value> methods = new HashMap<String, Value>();
	private ValueConverter converter;
	private List<Integer> generatedFunctions = new LinkedList<Integer>();
	private final GVMContext context;
	
	public NativeObjectWrapper(Object object, GVMContext context) {
		this.object = object;
		if(object instanceof RequiresGVMContext) {
			((RequiresGVMContext)object).setContext(context);
		}
		this.context = context;
		this.converter = context.getProgram().getConverter();
		for(Method m : object.getClass().getMethods()) {
			//Generate wrapper function
			NativeObjectMethodWrapper wrapperMethod = new NativeObjectMethodWrapper(m.getName(), object, m.getParameterCount());
			int nativeFunction = context.getProgram().add(wrapperMethod);
			RandomAccessByteStream code = new RandomAccessByteStream();
			List<String> paramNames = new LinkedList<String>();
			int i=1;
			for(Parameter parameter : m.getParameters()) {
				paramNames.add(parameter.getName());
				code.add(GVM.LDS);
				code.writeInt(i);
				i++;
			}
			//Generate a function to call the native method
			code.add(GVM.LDC_D);
			code.writeInt(nativeFunction);
			code.writeString(new FunctionType().getName());
			code.add(GVM.NATIVE);
			code.add(GVM.RETURN);
			GVMFunction function = new GVMFunction(code, paramNames);
			int index = context.getProgram().addFunction(function);
			generatedFunctions.add(index);
			generatedFunctions.add(nativeFunction);
			methods.put(m.getName(), new Value(index, new FunctionType(), "Generated function to call "+m.getName()+" on "+object.getClass().getName()));
		}
	}


	@Override
	public void setValue(String id, Value v) {
		//TODO: Currently not supported
	}

	@Override
	public Value getValue(String id) {
		try {
			Field field = object.getClass().getField(id);
			Object returnValue = field.get(object);
			return converter.convertToGVM(context, returnValue);
		} catch(NoSuchFieldException e) {
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if(methods.containsKey(id)) {
			return methods.get(id);
		} else {
			return new Value(0, new Undefined());
		}
	}

	public Object getObject() {
		return object;
	}
	
	@Override
	public Collection<Value> getValues() {
		return methods.values();
	}


	@Override
	public void preDestroy() {
		for(Integer functionId : generatedFunctions) {
			this.context.getProgram().deleteFunction(functionId);
		}
		
	}


	@Override
	public boolean hasValue(String id) {
		return methods.containsKey(id);
	}


	@Override
	public Collection<String> getKeys() {
		return methods.keySet();
	}
	
	@Override
	public NativeObjectWrapper clone() {
		return this;
	}
	
}
