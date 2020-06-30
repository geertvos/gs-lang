package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.lang.types.ArrayType;
import net.geertvos.gvm.lang.types.NumberType;

public class ArrayDefinitionExpression extends Expression implements Parameterizable {

	private final List<Expression> parameters = new LinkedList<Expression>();
	
	public ArrayDefinitionExpression() {
	}

	@Override
	public Parameterizable addParameter(Expression expression) {
		parameters.add(expression);
		return this;
	}

	@Override
	public void compile(GScriptCompiler c) {
		c.code.add(GVM.NEW);
		c.code.writeString(new ArrayType().getName());
		int counter  = 0;
		for(Expression e : parameters) {
			e.compile(c);
			c.code.add(GVM.LDS);
			c.code.writeInt(-1);
			c.code.add(GVM.LDC_D);
			c.code.writeInt(counter);
			c.code.writeString(new NumberType().getName());
			c.code.add(GVM.GET);
			c.code.add(GVM.PUT);
			c.code.add(GVM.POP);
			counter++;
		}
	}

}
