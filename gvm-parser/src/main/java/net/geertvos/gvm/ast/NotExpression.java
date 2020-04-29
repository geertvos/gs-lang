package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class NotExpression extends Expression {

	private Expression argument;
	
	public NotExpression( Expression argument )
	{
		this.argument = argument;
	}
	
	@Override
	public void compile(GCompiler c) {
		argument.compile(c);
		c.code.add(GVM.NOT);
	}

}
