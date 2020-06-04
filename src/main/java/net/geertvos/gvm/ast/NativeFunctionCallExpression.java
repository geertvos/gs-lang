package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.bridge.NativeMethodWrapper;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.FunctionType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.lang.bridge.NativeStaticMethodAutoWrapper;

public class NativeFunctionCallExpression extends Expression implements Parameterizable {

	private int identifier;
	private final List<Expression> parameters = new LinkedList<Expression>();
	
	public NativeFunctionCallExpression() {
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		NativeMethodWrapper method = new NativeStaticMethodAutoWrapper(parameters.size());
		identifier = c.getNativeMethodIndex(method);
		for( Expression e : parameters ) {
			e.compile(c);
		}
		c.code.add(GVM.LDC_D);
		c.code.writeInt(identifier);
		c.code.writeString(new FunctionType().getName());
		c.code.add(GVM.NATIVE);
//		c.code.add(GVM.RETURN); //TODO: Check why this line needs to be commented out
	}

	public Parameterizable addParameter(Expression expression) {
		parameters.add(expression);
		return this;
	}

}
