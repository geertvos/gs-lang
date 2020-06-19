package net.geertvos.gvm.lang.types;

import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Type;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.program.GVMContext;

public class ArrayType implements Type {

	@Override
	public String getName() {
		return "Array";
	}

	@Override
	public boolean supportsOperation(Operations op) {
		if(op.equals(Operations.INDEX)) {
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
		throw new IllegalArgumentException("Operation "+op+" not supported on Array type.");
	}

	@Override
	public Value perform(GVMContext context, Operations op, Value thisValue, Object parameter) {
		if(op.equals(Operations.INDEX)) {
			int index = (Integer)parameter;
			ArrayObject object = (ArrayObject) context.getHeap().getObject(thisValue.getValue());
			return object.getValue(index);
		}
		return null;
	}

}
