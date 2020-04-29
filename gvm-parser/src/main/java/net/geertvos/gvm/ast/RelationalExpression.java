package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class RelationalExpression extends TwoArgumentExpression {

	private final String operator;

	public RelationalExpression( Expression rhs , String operator, Expression lhs )
	{
		super(lhs,rhs);
		this.operator = operator.trim();
		if(!(this.operator.equals("<") 
				|| this.operator.equals(">")
				|| this.operator.equals(">=")
				|| this.operator.equals("<="))) {
			throw new IllegalArgumentException("Operator '"+operator+"' is not a valid relational operator.");
		}
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		if(operator.equalsIgnoreCase(">")) {
			super.compile(c);
			c.code.add(GVM.GT);
		} else if(operator.equalsIgnoreCase("<")) {
			super.compile(c);
			c.code.add(GVM.LT);
		} else if(operator.equalsIgnoreCase(">=")) {
			Expression e = new OrExpression(new RelationalExpression(getRhs(), ">", getLhs()), new EqualityExpression(getRhs(), "==", getLhs()));
			e.compile(c);
		} else if(operator.equalsIgnoreCase("<=")) {
			Expression e = new OrExpression(new RelationalExpression(getRhs(), "<", getLhs()), new EqualityExpression(getRhs(), "==", getLhs()));
			e.compile(c);
		} else {
			throw new IllegalStateException("Unknow operator: "+operator);
		}
	}
	
	public String getOperator() {
		return this.operator;
	}

}
