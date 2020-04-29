package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class ContinueStatement extends JumpStatement {

	@Override
	public void compile(GScriptCompiler c) {
		LoopStatement loop = c.peekLoop();
		loop.addContinue(this);
		super.compile(c);
	}

}
