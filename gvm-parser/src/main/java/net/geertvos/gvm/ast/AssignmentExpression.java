package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class AssignmentExpression extends Expression {

	private final Expression variable;
	private final Expression value;
	private String operator = "=";

	public AssignmentExpression( Expression value, Expression variable )
	{
		this.value = value;
		this.variable = variable;
	}
	
	public AssignmentExpression( Expression value, String operator, Expression variable )
	{
		this.value = value;
		this.variable = variable;
		this.operator = operator.trim();
	}
	
	public void compile(GScriptCompiler c) {
		if(operator.equalsIgnoreCase("=")) {
			value.compile(c);
			variable.compile(c);
			c.code.add(GVM.PUT);
		} else if(operator.equalsIgnoreCase("+=")) {
			variable.compile(c);
			value.compile(c);
			c.code.add(GVM.ADD);
			variable.compile(c);
			c.code.add(GVM.PUT);
		} else if(operator.equalsIgnoreCase("-=")) {
			variable.compile(c);
			value.compile(c);
			c.code.add(GVM.SUB);
			variable.compile(c);
			c.code.add(GVM.PUT);
		} else {
			throw new UnsupportedOperationException("Operator "+operator+" not implemented.");
		}
	}

	public String getOperator() {
		return operator;
	}
	
	public Expression getVariable() {
		return variable;
	}
	
	public Expression getValue() {
		return value;
	}
	
}
