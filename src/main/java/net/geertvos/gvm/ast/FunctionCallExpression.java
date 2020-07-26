package net.geertvos.gvm.ast;

import java.util.ArrayList;
import java.util.List;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class FunctionCallExpression extends Expression implements Parameterizable, FieldReferenceExpression {

	private final FieldReferenceExpression function;
	private final List<Expression> parameters;
	private Expression field;

	public FunctionCallExpression addParameter(Expression parameter) {
		this.parameters.add(parameter);
		return this;
	}
	
	public FieldReferenceExpression getFunction() {
		return function;
	}
	
	public int getParameterCount() {
		return parameters.size();
	}
	
	public Expression getParameter(int index) {
		return parameters.get(index);
	}
	
	public FunctionCallExpression( FieldReferenceExpression function , List<Expression> parameters )
	{
		this.function = function;
		this.parameters = parameters;
	}

	public FunctionCallExpression( FieldReferenceExpression function )
	{
		this.function = function;
		this.parameters = new ArrayList<Expression>();
	}

	@Override
	public void compile(GScriptCompiler c) {
		if( field== null ) //Add a pointer to this
		{
			c.code.add(GVM.LDS);
			c.code.writeInt(0);
		} else {
			field.compile(c);
		}
		//TODO: Compile in a parameter count check
		for( Expression e : parameters )
			e.compile(c);
		function.compile(c);
		c.code.add(GVM.INVOKE);
		c.code.writeInt(parameters.size());
	}

	@Override
	public FieldReferenceExpression setField(Expression e) {
		((FieldReferenceExpression)function).setField(e);
		return this;
	}

	@Override
	public Expression getField() {
		return function.getField();
	}
	
	

}
