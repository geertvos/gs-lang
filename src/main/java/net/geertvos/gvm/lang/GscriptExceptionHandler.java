package net.geertvos.gvm.lang;

import net.geertvos.gvm.core.GVMExceptionHandler;
import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.lang.types.ObjectType;
import net.geertvos.gvm.lang.types.NumberType;
import net.geertvos.gvm.lang.types.StringType;
import net.geertvos.gvm.program.GVMContext;

public class GscriptExceptionHandler implements GVMExceptionHandler {

	@Override
	public Value convert(String message, GVMContext context, int line, int location) {
		int index = context.getProgram().addString(message);
		Value exceptionMessage = new Value(index, new StringType());
		
		GVMObject exceptionObject = new GVMPlainObject();
		exceptionObject.setValue("message", exceptionMessage);
		exceptionObject.setValue("line", new Value(line, new NumberType()));
		exceptionObject.setValue("location", new Value(location, new StringType()));
		int id = context.getHeap().addObject(exceptionObject);
		return new Value(id, new ObjectType());
	}

	@Override
	public Value convert(Value value, GVMContext context, int line, int location) {
		//GScript wraps exceptions automatically in an Exception object
			GVMObject exceptionObject = new GVMPlainObject();
			if(value.getType() instanceof StringType) {
				exceptionObject.setValue("message", value);
			} else {
				exceptionObject.setValue("exception", value);
			}
			exceptionObject.setValue("line", new Value(line, new NumberType()));
			exceptionObject.setValue("location", new Value(location, new StringType()));
			int id = context.getHeap().addObject(exceptionObject);
			return new Value(id, new ObjectType());
	}

}
