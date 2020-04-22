package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;

public class AndExpression extends TwoArgumentExpression {

	public AndExpression( Expression rhs , Expression lhs )
	{
		super(lhs,rhs);
	}
	
	@Override
	public void compile(GCompiler c) {
		super.compile(c);
		c.code.add(GVM.AND);
	}

}
