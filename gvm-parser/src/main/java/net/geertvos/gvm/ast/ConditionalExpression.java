package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;

public class ConditionalExpression extends Expression {

	private final Expression condition;
	private final Expression positiveExpression;
	private final Expression negativeExpression;
	
	
	
	public ConditionalExpression(Expression negativeExpression, Expression positiveExpression, Expression condition) {
		super();
		this.condition = condition;
		this.positiveExpression = positiveExpression;
		this.negativeExpression = negativeExpression;
	}



	public Expression getCondition() {
		return condition;
	}



	public Expression getPositiveExpression() {
		return positiveExpression;
	}



	public Expression getNegativeExpression() {
		return negativeExpression;
	}



	@Override
	public void compile(GCompiler c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
