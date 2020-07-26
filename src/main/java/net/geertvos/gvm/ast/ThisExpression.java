package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ThisExpression extends Expression implements FieldReferenceExpression {

	private Expression field;
	
	public ThisExpression(){
		this(null);
	}
	
	public ThisExpression(Expression field){
		this.field = field;
	}

	@Override
	public void compile(GScriptCompiler c) {
		c.code.add(GVM.LDS);
		c.code.writeInt(0);
		if( field != null )
			field.compile(c);
	}

	@Override
	public FieldReferenceExpression setField(Expression e) {
		field = e;
		return this;
	}

	@Override
	public Expression getField() {
		return field;
	}
	
	
	
	
}
