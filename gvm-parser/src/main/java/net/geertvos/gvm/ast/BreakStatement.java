package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class BreakStatement extends JumpStatement {

	@Override
	public void compile(GScriptCompiler c) {
		LoopStatement loop = c.peekLoop();
		loop.addBreak(this);
		super.compile(c);
	}

}
