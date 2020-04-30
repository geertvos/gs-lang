package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ForStatement extends LoopStatement  {

	private final Expression condition;
	private final Expression initstatement;
	private final Expression updatestatement;
	private final Statement loop;

	public ForStatement( Statement statement, Expression update , Expression condition , Expression init, Position pos ) {
		super(pos);
		this.initstatement = init;
		this.loop = statement;
		this.condition = condition;
		this.updatestatement = update;
	}

	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		initstatement.compile(c);
		c.code.add(GVM.POP);
		int conditionpos = c.code.size();
		condition.compile(c);
		c.code.add( GVM.NOT );
		c.code.add( GVM.CJMP );
		int elsepos = c.code.size();
		c.code.writeInt( -1 ); 
		loop.compile(c);
		int updatepos = c.code.size();
		updatestatement.compile(c);
		c.code.add(GVM.POP);
		c.code.add( GVM.JMP );
		c.code.writeInt( conditionpos );
		c.code.set( elsepos, c.code.size());

		int endPos = c.code.size();
		for( JumpStatement js : breaks )
			js.setJump(endPos);
		for( JumpStatement js : continues )
			js.setJump(updatepos);
		
	}

}
