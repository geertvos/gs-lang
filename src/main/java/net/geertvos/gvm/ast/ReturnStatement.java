package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.Undefined;

public class ReturnStatement extends Statement {

	private Expression returnValue;
	
	public ReturnStatement(Position pos)
	{
		super(pos);
	}

	public ReturnStatement( Expression val, Position pos )
	{
		super(pos);
		this.returnValue = val;
	}

	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		if( returnValue == null )  {
			c.code.add(GVM.LDC_D);
			c.code.writeInt(0);
			c.code.writeString(new Undefined().getName());
		}
		else {
			returnValue.compile(c);
		}
		c.code.add(GVM.RETURN);
	}

	public Expression getReturnValue() {
		return returnValue;
	}

}
