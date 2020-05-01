package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ForStatement extends LoopStatement implements Scope {

	private final Expression condition;
	private final Expression initstatement;
	private final Expression updatestatement;
	private List<Statement> loop = new LinkedList<Statement>();

	public ForStatement( Statement statement, Expression update , Expression condition , Expression init, Position pos ) {
		super(pos);
		this.initstatement = init;
		this.loop.add(statement);
		this.condition = condition;
		this.updatestatement = update;
	}

	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		initstatement.compile(c);
		int conditionpos = c.code.size();
		condition.compile(c);
		c.code.add( GVM.NOT );
		c.code.add( GVM.CJMP );
		int elsepos = c.code.size();
		c.code.writeInt( -1 ); 
		for(Statement s : loop) {
			s.compile(c); //TODO: check this after changes made
		}
		int updatepos = c.code.size();
		updatestatement.compile(c);
		c.code.add( GVM.JMP );
		c.code.writeInt( conditionpos );
		c.code.set( elsepos, c.code.size());

		int endPos = c.code.size();
		for( JumpStatement js : breaks )
			js.setJump(endPos);
		for( JumpStatement js : continues )
			js.setJump(updatepos);
		
	}


	public Scope addStatement(Statement statement) {
		loop.add(statement);
		return this;
	}


	public Statement getStatement(int index) {
		return loop.get(index);
	}


	public int getStatements() {
		return loop.size();
	}	
	
}
