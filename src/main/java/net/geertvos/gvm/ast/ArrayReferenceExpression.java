package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ArrayReferenceExpression extends Expression {

	private Expression index;
	private Expression reference;

	public ArrayReferenceExpression(Expression index, Expression reference) {
		this.index = index;
		this.reference = reference;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		reference.compile(c);
		index.compile(c);
		c.code.add(GVM.GET);
	}

}
