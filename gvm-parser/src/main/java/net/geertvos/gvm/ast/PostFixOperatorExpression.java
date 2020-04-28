package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;
import nl.gvm.core.Value;

public class PostFixOperatorExpression extends Expression {

	private final Expression argument;
	private final String operator;
	
	public PostFixOperatorExpression(String operator, Expression argument) {
		this.argument = argument;
		this.operator = operator.trim();
	}
	
	@Override
	public void compile(GCompiler c) {
		if(operator.equalsIgnoreCase("++")) {
			//Hack to make sure we 'return' the previous value of the postfix
			AdditiveExpression add0 = new AdditiveExpression(new ConstantExpression(0,  Value.TYPE.NUMBER),"+", argument);
			add0.compile(c);

			AdditiveExpression add = new AdditiveExpression(new ConstantExpression(1,  Value.TYPE.NUMBER),"+", argument);
			AssignmentExpression assignment = new AssignmentExpression(add, argument);
			assignment.compile(c);
			c.code.add(GVM.POP); 
		} else if(operator.equalsIgnoreCase("--")) {
			//Hack to make sure we 'return' the previous value of the postfix
			AdditiveExpression add0 = new AdditiveExpression(new ConstantExpression(0,  Value.TYPE.NUMBER),"+", argument);
			add0.compile(c);
			AdditiveExpression add = new AdditiveExpression(new ConstantExpression(1,  Value.TYPE.NUMBER),"-", argument);
			AssignmentExpression assignment = new AssignmentExpression(add, argument);
			assignment.compile(c);
			c.code.add(GVM.POP); 
		} else {
			throw new IllegalArgumentException("Operator is not a valud PostFixOperator: "+operator);
		}
	}

	
}