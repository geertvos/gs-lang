package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;

public class EqualityExpression extends TwoArgumentExpression {

	private final String operator;

	public EqualityExpression( Expression rhs , String operator, Expression lhs )
	{
		super(lhs,rhs);
		this.operator = operator.trim();
		if(!(this.operator.equals("==") || this.operator.equals("!="))) {
			throw new IllegalArgumentException("Operator '"+operator+"' is not a valid equality operator.");
		}
	}
	
	@Override
	public void compile(GCompiler c) {
		super.compile(c);
		if(operator.equalsIgnoreCase("==")) {
			c.code.add(GVM.EQL);
		}
		else if(operator.equalsIgnoreCase("!=")) {
			c.code.add(GVM.EQL);
			c.code.add(GVM.NOT);
		} else {
			throw new IllegalStateException("Unknow operator: "+operator);
		}
	}
	
	public String getOperator() {
		return this.operator;
	}

}
