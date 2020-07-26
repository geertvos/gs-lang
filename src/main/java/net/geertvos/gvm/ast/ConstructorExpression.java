package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class ConstructorExpression extends Expression {

	private final FunctionCallExpression function;
	
	public ConstructorExpression( FunctionCallExpression function )
	{
		this.function = function;
	}

	public FunctionCallExpression getFunction() {
		return function;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		new ConstantExpression().compile(c);
		function.compile(c);
	}

}
