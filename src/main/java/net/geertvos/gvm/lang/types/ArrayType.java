package net.geertvos.gvm.lang.types;

import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Type;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.program.GVMContext;

public class ArrayType implements Type {

	@Override
	public String getName() {
		return "Array";
	}

	@Override
	public boolean supportsOperation(Operations op) {
		if(op.equals(Operations.GET)) {
			return true;
		}
		if(op.equals(Operations.NEW)) {
			return true;
		}
		return false;
	}

	@Override
	public Value perform(GVMContext context, Operations op, Value thisValue, Value otherValue) {
		if(op.equals(Operations.NEW)) {
			Integer id = context.getHeap().addObject(new ArrayObject());
			return new Value(id, new ArrayType());
		}
		if(op.equals(Operations.GET)) {
			if(otherValue.getType().isInstance(new StringType())) {
				String parameter = context.getProgram().getString(otherValue.getValue());
				if(parameter.equals("length")) {
					ArrayObject array = (ArrayObject) context.getHeap().getObject(thisValue.getValue());
					return new Value(array.getLength(), new NumberType());
				} else {
					return new Value(0, new Undefined());
				}
			}
			else if(otherValue.getType().isInstance(new NumberType())) {
				ArrayObject array = (ArrayObject) context.getHeap().getObject(thisValue.getValue());
				return array.getValue(otherValue.getValue());
			}
		}
		throw new IllegalArgumentException("Operation "+op+" not supported on Array type.");
	}

	@Override
	public boolean isInstance(Type otherType) {
		if(otherType.getName().equals(getName())) {
			return true;
		}
		return false;
	}


}
