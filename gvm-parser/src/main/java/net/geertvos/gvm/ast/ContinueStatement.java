package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;

public class ContinueStatement extends JumpStatement {

	@Override
	public void compile(GCompiler c) {
		LoopStatement loop = c.peekLoop();
		loop.addContinue(this);
		super.compile(c);
	}

}
