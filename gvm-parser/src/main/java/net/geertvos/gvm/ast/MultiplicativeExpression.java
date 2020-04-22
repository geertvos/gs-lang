package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;

public class MultiplicativeExpression extends TwoArgumentExpression {

	private final String operator;
	
	public MultiplicativeExpression( Expression rhs , String operator, Expression lhs )
	{
		super(lhs,rhs);
		this.operator = operator.trim();
		if(!(this.operator.equals("*") || this.operator.equals("/")|| this.operator.equals("%"))) {
			throw new IllegalArgumentException("Operator '"+operator+"' is not a valid multiplicative operator.");
		}

	}
	
	@Override
	public void compile(GCompiler c) {
		super.compile(c);
		if(operator.equalsIgnoreCase("*")) {
			c.code.add(GVM.MULT);
		}
		else if(operator.equalsIgnoreCase("/")) {
			c.code.add(GVM.DIV);
		} else if(operator.equalsIgnoreCase("%")) {
			throw new UnsupportedOperationException("Modules not implemented in GVM yet.");
		} else {
			throw new IllegalStateException("Unknow operator: "+operator);
		}
	}

	public String getOperator() {
		return operator;
	}

}
