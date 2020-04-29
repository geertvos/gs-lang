package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class ExpressionStatement extends Statement {

	private final Expression expression;
	
	public ExpressionStatement( Expression e )
	{
		this.expression = e;
	}
	
	@Override
	public void compile(GCompiler c) {
		expression.compile(c);
		c.code.add( GVM.POP );
	}

	public Expression getExpression() {
		return expression;
	}
	
}
