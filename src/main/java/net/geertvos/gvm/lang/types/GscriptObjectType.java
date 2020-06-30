package net.geertvos.gvm.lang.types;

import net.geertvos.gvm.core.BooleanType;
import net.geertvos.gvm.core.Type;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.lang.GVMPlainObject;
import net.geertvos.gvm.program.GVMContext;

public class GscriptObjectType implements Type {

	@Override
	public Value perform(GVMContext context, Operations op, Value thisValue, Object parameter) {
		if(op != Operations.GET) {
			throw new IllegalArgumentException("Operation " + op + " is not supported by type " + getName());
		}
		if(parameter.equals("ref")) {
			return new Value(thisValue.getValue(), new NumberType());
		}
		return context.getHeap().getObject(thisValue.getValue()).getValue((String) parameter);
	}

	@Override
	public String getName() {
		return "Object";
	}

	@Override
	public boolean supportsOperation(Operations op) {
		if(op.equals(Operations.NEW)) {
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
		if(op.equals(Operations.EQL)) {
			//TODO: Check if this always works
			return new Value(thisValue.getValue() == otherValue.getValue()?1:0, new BooleanType());
		}
		if(op.equals(Operations.GET)) {
			int ref = otherValue.getValue();
			if(otherValue.getType().isInstance(new StringType())) {
				String name = context.getProgram().getString(ref);
				if(name.equals("ref")) {
					return new Value(thisValue.getValue(), new NumberType());
				} else {
					return context.getHeap().getObject(thisValue.getValue()).getValue(name);
				}
			}
			throw new IllegalArgumentException("Operation "+op+" not supported on "+getName()+" with type "+otherValue.getType());
		}
		if(op.equals(Operations.NEW)) {
			int id = context.getHeap().addObject(new GVMPlainObject());
			return new Value(id, new GscriptObjectType());
		}
		return null;
	}
	
	@Override
	public boolean isInstance(Type otherType) {
		if(otherType.getName().equals(getName())) {
			return true;
		}
		return false;
	}


	
}
