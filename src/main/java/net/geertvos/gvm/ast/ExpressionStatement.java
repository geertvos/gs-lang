package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ExpressionStatement extends Statement {

	private final Expression expression;
	
	public ExpressionStatement( Expression e, Position pos )
	{
		super(pos);
		this.expression = e;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		expression.compile(c);
		c.code.add( GVM.POP );
	}

	public Expression getExpression() {
		return expression;
	}
	
}
