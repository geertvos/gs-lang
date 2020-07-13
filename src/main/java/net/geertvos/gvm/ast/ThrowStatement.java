package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ThrowStatement extends Statement {

	private final Expression exception;
	
	public ThrowStatement(Expression exception, Position pos) {
		super(pos);
		this.exception = exception;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		exception.compile(c);
		c.code.add(GVM.THROW);
	}

}
