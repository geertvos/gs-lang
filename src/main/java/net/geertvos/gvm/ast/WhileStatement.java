package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class WhileStatement extends LoopStatement{

	private final Expression condition;
	private final Statement statement;
	
	public WhileStatement( Statement loop, Expression condition, Position pos )
	{
		super(pos);
		this.condition = condition;
		this.statement = loop;
	}	
	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		int pos = c.code.size();
		
		condition.compile(c);
		c.pushLoop(this);
		
		c.code.add( GVM.NOT );
		c.code.add( GVM.CJMP );
		int placepos = c.code.size();
		c.code.writeInt( -1 ); // Placeholder at pos+2
		statement.compile(c);
		c.code.add( GVM.JMP );
		c.code.writeInt( pos );
		
		//Set placeholder value
		int endPos = c.code.size();
		c.code.set( placepos, endPos );
		for( JumpStatement js : breaks )
			js.setJump(endPos);
		for( JumpStatement js : continues )
			js.setJump(pos);
		c.popLoop();
		
	}

}
