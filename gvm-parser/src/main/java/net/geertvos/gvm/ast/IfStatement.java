package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class IfStatement extends Statement implements Scope {

	private Expression condition;
	private Statement truestatement;
	private Statement falsestatement;
	
	public IfStatement( Statement truestatement, Expression condition)
	{
		this.condition = condition;
		this.truestatement = truestatement;
	}
	
	public IfStatement( Statement falsestatement,Statement truestatement, Expression condition)
	{
		this.condition = condition;
		this.truestatement = truestatement;
		this.falsestatement = falsestatement;
	}
	
	
	@Override
	public void compile(GCompiler c) {
		condition.compile(c);
		c.code.add( GVM.NOT );
		c.code.add( GVM.CJMP );
		int elsepos = c.code.getPointerPosition();
		c.code.writeInt( -1 ); 
		truestatement.compile(c);
		if( falsestatement != null )
		{
			c.code.add( GVM.JMP );
			int endoftrue = c.code.getPointerPosition();
			c.code.writeInt( -1 );
			c.code.set(elsepos, c.code.getPointerPosition());
			falsestatement.compile(c);
			c.code.set(endoftrue, c.code.getPointerPosition());
		} else {
			c.code.set(elsepos, c.code.getPointerPosition());
		}
	}

	@Override
	public Scope addStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement getStatement(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatements() {
		// TODO Auto-generated method stub
		return 0;
	}

}
