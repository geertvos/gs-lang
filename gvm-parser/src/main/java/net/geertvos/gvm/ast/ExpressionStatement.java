package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ExpressionStatement extends Statement {

	private final Expression expression;
	
	public ExpressionStatement( Expression e )
	{
		this.expression = e;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		expression.compile(c);
		c.code.add( GVM.POP );
	}

	public Expression getExpression() {
		return expression;
	}
	
}
