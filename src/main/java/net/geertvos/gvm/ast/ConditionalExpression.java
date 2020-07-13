package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

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
	public void compile(GScriptCompiler c) {
		condition.compile(c);
		c.code.add( GVM.NOT );
		c.code.add( GVM.CJMP );
		int elsepos = c.code.getPointerPosition();
		c.code.writeInt( -1 ); 
		positiveExpression.compile(c);
		c.code.add( GVM.JMP );
		int endoftrue = c.code.getPointerPosition();
		c.code.writeInt( -1 );
		c.code.set(elsepos, c.code.getPointerPosition());
		negativeExpression.compile(c);
		c.code.set(endoftrue, c.code.getPointerPosition());
	}

}
