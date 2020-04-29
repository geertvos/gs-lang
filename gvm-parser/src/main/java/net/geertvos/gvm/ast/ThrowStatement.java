package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class ThrowStatement extends Statement {

	private Expression exception;
	
	public ThrowStatement(Expression exception) {
		this.exception = exception;
	}
	
	@Override
	public void compile(GCompiler c) {
		exception.compile(c);
		c.code.add(GVM.THROW);
	}

}
