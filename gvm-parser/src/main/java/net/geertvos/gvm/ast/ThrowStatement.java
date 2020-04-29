package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ThrowStatement extends Statement {

	private Expression exception;
	
	public ThrowStatement(Expression exception) {
		this.exception = exception;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		exception.compile(c);
		c.code.add(GVM.THROW);
	}

}
