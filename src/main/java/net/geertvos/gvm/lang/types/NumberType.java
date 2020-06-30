package net.geertvos.gvm.lang.types;

import net.geertvos.gvm.core.BooleanType;
import net.geertvos.gvm.core.Type;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.core.Type.Operations;
import net.geertvos.gvm.program.GVMContext;

public class NumberType implements Type {

	@Override
	public String getName() {
		return "Number";
	}

	@Override
	public boolean supportsOperation(Operations op) {
		if(op.equals(Operations.ADD)) {
			return true;
		}
		if(op.equals(Operations.SUB)) {
			return true;
		}
		if(op.equals(Operations.MULT)) {
			return true;
		}
		if(op.equals(Operations.DIV)) {
			return true;
		}
		if(op.equals(Operations.MOD)) {
			return true;
		}
		if(op.equals(Operations.NOT)) {
			return true;
		}
		if(op.equals(Operations.EQL)) {
			return true;
		}
		if(op.equals(Operations.GT)) {
			return true;
		}
		if(op.equals(Operations.LT)) {
			return true;
		}
		return false;
	}

	@Override
	public Value perform(GVMContext context, Operations op, Value thisValue, Value otherValue) {
		if (op.equals(Operations.ADD)) {
			if (otherValue.getType() instanceof NumberType) {
				return new Value(thisValue.getValue() + otherValue.getValue(), new NumberType());
			}
			if (otherValue.getType() instanceof StringType) {
				String arg1 = "" + thisValue.getValue();
				String arg2 = context.getProgram().getString(otherValue.getValue());
				int index = context.getProgram().addString(arg1 + arg2);
				return new Value(index, new StringType());
			}
		} else if (op.equals(Operations.SUB)) {
			return new Value(thisValue.getValue() - otherValue.getValue(), new NumberType());
		} else if (op.equals(Operations.MULT)) {
			return new Value(thisValue.getValue() * otherValue.getValue(), new NumberType());
		} else if (op.equals(Operations.DIV)) {
			return new Value(thisValue.getValue() / otherValue.getValue(), new NumberType());
		} else if (op.equals(Operations.MOD)) {
			return new Value(thisValue.getValue() % otherValue.getValue(), new NumberType());
		} else if (op.equals(Operations.EQL)) {
			return new Value(thisValue.getValue() == otherValue.getValue() ? 1 : 0, new BooleanType());
		} else if (op.equals(Operations.LT)) {
			return new Value(thisValue.getValue() < otherValue.getValue() ? 1 : 0, new BooleanType());
		} else if (op.equals(Operations.GT)) {
			return new Value(thisValue.getValue() > otherValue.getValue() ? 1 : 0, new BooleanType());
		}
		throw new IllegalArgumentException("Operation " + op + " is not supported by type " + getName());
	}

	@Override
	public Value perform(GVMContext context, Operations op, Value thisValue, Object parameter) {
		// TODO Auto-generated method stub
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
