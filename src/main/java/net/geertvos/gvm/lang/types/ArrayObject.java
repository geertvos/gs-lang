package net.geertvos.gvm.lang.types;

import java.util.Collection;

import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.core.Value;

public class ArrayObject implements GVMObject {

	private Value[] values = new Value[0];
	
	@Override
	public void setValue(String id, Value v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Value getValue(String id) {
		return new Value(0, new Undefined());
	}

	public void setValue(int index, Value v) {
		values[index] = v;
	}

	public Value getValue(int id) {
		if(id>=values.length) {
			Value[] newArray = new Value[id+1];
			for(int x=0;x<values.length;x++) {
				newArray[x] = values[x];
			}
			values = newArray;
		}
		if(values[id] == null) {
			Value v = new Value(0, new Undefined());
			values[id]= v;
			return v;
		}
		return values[id];
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

	public int getLength() {
		return values.length;
	}

}
