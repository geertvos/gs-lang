package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;

public class ThisExpression extends Expression {

	private VariableExpression field;
	
	public ThisExpression(){
	}
	
	public ThisExpression(VariableExpression field){
		this.field = field;
	}

	@Override
	public void compile(GCompiler c) {
		c.code.add(GVM.LDS);
		c.code.writeInt(0);
		if( field != null )
			field.compile(c);
	}
	
	
	
	
}
