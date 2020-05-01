package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class VoidStatement extends Statement {

	protected VoidStatement(Position pos) {
		super(pos);
	}

	@Override
	public void compile(GScriptCompiler c) {
	}

}
