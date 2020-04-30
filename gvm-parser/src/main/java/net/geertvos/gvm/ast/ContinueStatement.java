package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class ContinueStatement extends JumpStatement {

	public ContinueStatement(Position pos) {
		super(pos);
	}

	@Override
	public void compile(GScriptCompiler c) {
		LoopStatement loop = c.peekLoop();
		loop.addContinue(this);
		super.compile(c);
	}

}
