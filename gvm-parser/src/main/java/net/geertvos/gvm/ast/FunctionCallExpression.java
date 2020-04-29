package net.geertvos.gvm.ast;

import java.util.ArrayList;
import java.util.List;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class FunctionCallExpression extends Expression implements FieldReferenceExpression, Parameterizable {

	private final FieldReferenceExpression function;
	private final List<Expression> parameters;
	private Expression field;

	
	public FunctionCallExpression( Expression field, FieldReferenceExpression function , List<Expression> parameters )
	{
		this.field = field;
		this.function = function;
		this.parameters = parameters;
	}	
	
	public FunctionCallExpression addParameter(Expression parameter) {
		this.parameters.add(parameter);
		return this;
	}
	
	public Expression getField() {
		return field;
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
	
	public FieldReferenceExpression setField( Expression field )
	{
		if( this.field == null )
		{
			this.field = field;
			this.function.setField(field);
		} else {
			((FieldReferenceExpression)this.field).setField(field);
		}
		return this;
	}

	public void setFieldOnly( Expression field )
	{
		this.field = field;
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
	public void compile(GCompiler c) {
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

}
