package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class BreakStatement extends JumpStatement {

	public BreakStatement(Position pos) {
		super(pos);
	}

	@Override
	public void compile(GScriptCompiler c) {
		LoopStatement loop = c.peekLoop();
		loop.addBreak(this);
		super.compile(c);
	}

}
