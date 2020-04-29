package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class ConstructorExpression extends Expression {

	private final FunctionCallExpression function;
	
	public ConstructorExpression( FunctionCallExpression function )
	{
		this.function = function;
		function.setFieldOnly( new ConstantExpression() );	
	}

	public FunctionCallExpression getFunction() {
		return function;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		function.compile(c);
	}

}
