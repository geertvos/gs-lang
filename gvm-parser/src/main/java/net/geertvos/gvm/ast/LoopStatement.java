package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import org.parboiled.support.Position;

public abstract class LoopStatement extends Statement {

	protected final List<JumpStatement> breaks = new LinkedList<JumpStatement>();
	protected final List<JumpStatement> continues = new LinkedList<JumpStatement>();
	
	protected LoopStatement(Position pos) {
		super(pos);
	}
	
	public void addBreak( JumpStatement b )
	{
		breaks.add(b);
	}
	
	public void addContinue( JumpStatement c )
	{
		continues.add(c);
	}
}
