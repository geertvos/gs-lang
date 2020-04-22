package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;

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
	
	public void compile(GCompiler c) {
		value.compile(c);
		variable.compile(c);
		c.code.add(GVM.PUT);
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
