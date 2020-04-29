package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class IfStatement extends Statement {

	private final Expression condition;
	private final Statement thenClause;
	private final Statement elseClause;
	
	public IfStatement( Statement thenClause, Expression condition)
	{
		this.condition = condition;
		this.thenClause = thenClause;
		this.elseClause = null;
	}
	
	public IfStatement( Statement elseClause, Statement thenClause, Expression condition)
	{
		this.condition = condition;
		this.thenClause = thenClause;
		this.elseClause = elseClause;
	}
	
	public Statement getThenClause() {
		return thenClause;
	}
	
	public Statement getElseClause() {
		return elseClause;
	}
	
	
	@Override
	public void compile(GCompiler c) {
		condition.compile(c);
		c.code.add( GVM.NOT );
		c.code.add( GVM.CJMP );
		int elsepos = c.code.getPointerPosition();
		c.code.writeInt( -1 ); 
		thenClause.compile(c);
		if( elseClause != null )
		{
			c.code.add( GVM.JMP );
			int endoftrue = c.code.getPointerPosition();
			c.code.writeInt( -1 );
			c.code.set(elsepos, c.code.getPointerPosition());
			elseClause.compile(c);
			c.code.set(endoftrue, c.code.getPointerPosition());
		} else {
			c.code.set(elsepos, c.code.getPointerPosition());
		}
	}


}
