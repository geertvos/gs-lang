package net.geertvos.gvm.lang.types;

import java.util.Collection;

import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.core.Value;

public class ArrayObject implements GVMObject {

	
	@Override
	public void setValue(String id, Value v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Value getValue(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValue(int index, Value v) {
		// TODO Auto-generated method stub
		
	}

	public Value getValue(int id) {
		//TODO: store
		Value v = new Value(0, new Undefined());
		return v;
	}

	@Override
	public boolean hasValue(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Value> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void preDestroy() {
		// TODO Auto-generated method stub
		
	}

}
