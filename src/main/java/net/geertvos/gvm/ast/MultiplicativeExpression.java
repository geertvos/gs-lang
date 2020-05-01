package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

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
	public void compile(GScriptCompiler c) {
		super.compile(c);
		if(operator.equalsIgnoreCase("*")) {
			c.code.add(GVM.MULT);
		}
		else if(operator.equalsIgnoreCase("/")) {
			c.code.add(GVM.DIV);
		} else if(operator.equalsIgnoreCase("%")) {
			c.code.add(GVM.MOD);
		} else {
			throw new IllegalStateException("Unknow operator: "+operator);
		}
	}

	public String getOperator() {
		return operator;
	}

}
