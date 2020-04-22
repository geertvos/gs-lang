package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;
import nl.gvm.streams.RandomAccessByteStream;

public class JumpStatement extends Statement {

	private int jumpPos;
	private RandomAccessByteStream code;
	
	public void setJump( int jump )
	{
		int oldpos = code.getPointerPosition();
		code.seek(jumpPos);
		code.writeInt(jump);
		code.seek(oldpos);
	}
	
	@Override
	public void compile(GCompiler c) {
		c.code.write(GVM.JMP);
		jumpPos = c.code.size();
		c.code.writeInt(-1);
		this.code = c.code;
	}

}
