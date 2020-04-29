package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class AdditiveExpression extends TwoArgumentExpression {

	private final String operator;
	
	public AdditiveExpression( Expression rhs , String operator, Expression lhs )
	{
		super(lhs,rhs);
		this.operator = operator.trim();
		if(!(this.operator.equals("+") || this.operator.equals("-"))) {
			throw new IllegalArgumentException("Operator '"+operator+"' is not a valid additive operator.");
		}

	}
	
	@Override
	public void compile(GCompiler c) {
		super.compile(c);
		if(operator.equalsIgnoreCase("+")) {
			c.code.add(GVM.ADD);
		}
		else if(operator.equalsIgnoreCase("-")) {
			c.code.add(GVM.SUB);
		} else {
			throw new IllegalStateException("Unknow operator: "+operator);
		}
	}

	public String getOperator() {
		return operator;
	}

}
