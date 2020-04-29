package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.streams.RandomAccessByteStream;

public class BreakStatement extends JumpStatement {

	@Override
	public void compile(GCompiler c) {
		LoopStatement loop = c.peekLoop();
		loop.addBreak(this);
		super.compile(c);
	}

}