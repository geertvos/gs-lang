package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class TwoArgumentExpression extends Expression {

	private final Expression lhs;
	private final Expression rhs;
	
	
	public TwoArgumentExpression( Expression lhs , Expression rhs )
	{
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		lhs.compile(c);
		rhs.compile(c);
	}

	public Expression getLhs() {
		return lhs;
	}

	public Expression getRhs() {
		return rhs;
	}

}
