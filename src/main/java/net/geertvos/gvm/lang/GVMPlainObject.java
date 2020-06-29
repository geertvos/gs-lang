package net.geertvos.gvm.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.core.Value;

/**
 * The GVMObject represents an object created by the virtual machine and is always placed on the heap. The stack may contain references to an object.
 * Each object has a number of values stored using an integer as ID. The compiler maps field/function names to an id.
 * @author geertvos
 *
 */
public class GVMPlainObject implements GVMObject {

	private final Map<String,Value> data = new HashMap<String, Value>();
	
	/**
	 * Set a value in this object
	 * @param id The ID of the field
	 * @param v The value, can be a reference to another object.
	 */
	@Override
	public void setValue( String name , Value v )
	{
		data.put(name, v);
	}
	
	/**
	 * Returns the value stored under ID 
	 * @param id The id of the field/function
	 * @return Returns the value as Value object and Undefined if not found.
	 */
	@Override
	public Value getValue( String name )
	{
		if(data.containsKey(name))
			return data.get(name);
		else {
			Value v = new Value(0, new Undefined());
			data.put( name , v );
			return v;
		}
	}

	/**
	 * Return all the fields/functions of this objects. For garbage collection purposes.
	 * @return A collection of all fields. 
	 */
	@Override
	public Collection<Value> getValues() {
		return data.values();
	}

	@Override
	public String toString() {
		return "GVMPlainObject [data=" + data + "]";
	}

	@Override
	public void preDestroy() {
	}

	@Override
	public boolean hasValue(String id) {
		return data.containsKey(id);
	}
	
}
