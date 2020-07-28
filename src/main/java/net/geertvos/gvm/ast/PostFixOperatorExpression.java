package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.lang.types.NumberType;

public class PostFixOperatorExpression extends Expression {

	private final Expression argument;
	private final String operator;
	
	public PostFixOperatorExpression(String operator, Expression argument) {
		this.argument = argument;
		this.operator = operator.trim();
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		if(operator.equalsIgnoreCase("++")) {
			//Hack to make sure we 'return' the previous value of the postfix
			AdditiveExpression add0 = new AdditiveExpression(new ConstantExpression(0,  new NumberType().getName()),"+", argument);
			add0.compile(c);

			AdditiveExpression add = new AdditiveExpression(new ConstantExpression(1,  new NumberType().getName()),"+", argument);
			AssignmentExpression assignment = new AssignmentExpression(add, argument);
			assignment.compile(c);
			c.code.add(GVM.POP); 
		} else if(operator.equalsIgnoreCase("--")) {
			//Hack to make sure we 'return' the previous value of the postfix
			AdditiveExpression add0 = new AdditiveExpression(new ConstantExpression(0,  new NumberType().getName()),"+", argument);
			add0.compile(c);
			AdditiveExpression add = new AdditiveExpression(new ConstantExpression(1,  new NumberType().getName()),"-", argument);
			AssignmentExpression assignment = new AssignmentExpression(add, argument);
			assignment.compile(c);
			c.code.add(GVM.POP); 
		} else {
			throw new IllegalArgumentException("Operator is not a valud PostFixOperator: "+operator);
		}
	}

	
}
