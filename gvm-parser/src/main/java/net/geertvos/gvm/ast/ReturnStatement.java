package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ReturnStatement extends Statement {

	private List<Expression> returnValues;
	
	public ReturnStatement()
	{
		
	}

	public ReturnStatement( Expression val )
	{
		this.returnValues = new LinkedList<Expression>();
		this.returnValues.add(val);
	}

	
	public ReturnStatement(List<Expression> returnValues) {
		this.returnValues = returnValues;
	}

	public Expression getExpression(int index) {
		return returnValues.get(index);
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		if( returnValues == null )
			c.code.add(GVM.LDC_U);
		else {
			if(returnValues.size() == 1) {
				returnValues.get(0).compile(c);
			} else {
				//Wrap multiple return values in an object and set fields by index
				c.code.add(GVM.NEW);
				for(int i=0;i<returnValues.size();i++) {
					returnValues.get(i).compile(c); //this line is in the wrong spot
					c.code.add(GVM.LDS);
					c.code.writeInt(-1);
					c.code.add(GVM.GET);
					c.code.writeString("field_"+i);
					c.code.add(GVM.PUT);
					c.code.add(GVM.POP);
				}
			}
		}
		c.code.add(GVM.RETURN);
	}

}
