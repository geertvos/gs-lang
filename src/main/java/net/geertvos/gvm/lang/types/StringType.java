package net.geertvos.gvm.lang.types;

import net.geertvos.gvm.core.BooleanType;
import net.geertvos.gvm.core.Type;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.core.Type.Operations;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.lang.bridge.NativeObjectWrapper;
import net.geertvos.gvm.program.GVMContext;

public class StringType implements Type {

	@Override
	public String getName() {
		return "String";
	}

	@Override
	public boolean supportsOperation(Operations op) {
		if(op.equals(Operations.ADD)) {
			return true;
		}
		if(op.equals(Operations.GET)) {
			return true;
		}
		if(op.equals(Operations.EQL)) {
			return true;
		}
		return false;
	}

	@Override
	public Value perform(GVMContext context, Operations op, Value thisValue, Value otherValue) {
		if(op.equals(Operations.GET)) {
			int ref = otherValue.getValue();
			if(otherValue.getType().isInstance(new StringType())) {
				String parameter = context.getProgram().getString(ref);
				if(parameter.equals("lowercase")) {
					String lowered = context.getProgram().getString(thisValue.getValue()).toLowerCase();
					int index = context.getProgram().addString(lowered);
					return new Value(index, new StringType());
				}
				if(parameter.equals("length")) {
					String s = context.getProgram().getString(thisValue.getValue());
					return new Value(s.length(), new NumberType());
				}
				if(parameter.equals("bytes")) {
					String s = context.getProgram().getString(thisValue.getValue());
					int index = context.getHeap().addObject(new NativeObjectWrapper(s.getBytes(), context));
					return new Value(index, new GscriptObjectType());
				}
				if(parameter.equals("ref")) {
					return new Value(thisValue.getValue(), new NumberType());
				}
			}
			return new Value(thisValue.getValue() == otherValue.getValue()?1:0, new BooleanType());
		}
		else if(op.equals(Operations.ADD)) {
			String arg1 = context.getProgram().getString(thisValue.getValue());
			String arg2 = "";
			if(otherValue.getType() instanceof StringType) {
				arg2 = context.getProgram().getString(otherValue.getValue());
			}
			else if(otherValue.getType() instanceof NumberType) {
				arg2 = ""+otherValue.getValue();
			}
			else if(otherValue.getType() instanceof BooleanType) {
				arg2 = otherValue.getValue()>0?"true":"false";
			} else {
				arg2 = otherValue.getType().getName()+"("+otherValue.getValue()+")";
			}
			int val = context.getProgram().addString(arg1+arg2);
			return new Value(val, new StringType());
		} else if(op.equals(Operations.EQL)) {
			return new Value(thisValue.getValue() == otherValue.getValue()?1:0, new BooleanType());
		} else {
			throw new IllegalArgumentException("Operation "+op+" not supported on "+getName());
		}
	}

	@Override
	public Value perform(GVMContext context, Operations op, Value thisValue, Object parameter) {
		//Implement built in functions
		return new Value(0, new Undefined());
	}
	
	@Override
	public boolean isInstance(Type otherType) {
		if(otherType.getName().equals(getName())) {
			return true;
		}
		return false;
	}


}
