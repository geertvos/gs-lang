package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;

public class VariableExpression extends Expression implements FieldReferenceExpression {

	private final String name;
	private Expression field;
	
	/**
	 * Construct a variable pointing to this.<name>
	 * Non existing fields will be created
	 * @param name The name of the variable
	 */
	public VariableExpression(String name){
		this.name = name.trim();
	}
	
	/**
	 * Construct a variable <field>.name
	 * Non existing fields will be created
	 * @param name The name of the variable
	 * @param field The field to which the variable belongs
	 */
	public VariableExpression(String name,Expression field){
		this(name);
		this.field = field;
	}

	public FieldReferenceExpression setField( Expression field )
	{
		if( this.field!=null)
			((FieldReferenceExpression) this.field).setField(field);
		else this.field = field;
		return this;
	}
	
	public void compile(GCompiler c) {
		if( c.getFunction().getParameters().contains(name) && field==null )
		{
			//Variable points to a parameter
			c.code.add(GVM.LDS);
			c.code.writeInt(1+c.getFunction().getParameters().indexOf(name)); 
		} else if( c.getFunction().getLocals().contains(name) && field==null ) {
			//Variable points to a local variable
			c.code.add(GVM.LDS);
			c.code.writeInt(1+c.getFunction().getParameters().size()+c.getFunction().getLocals().indexOf(name)); 
		} else {
			//Variable points to a field of this
			if( field!=null )
				field.compile(c);
			else {
				c.code.add(GVM.LDS);
				c.code.writeInt(0); //Load this
			}
			c.code.add(GVM.GET);
			c.code.writeString(name);
		}
	}

	public String getName() {
		return name;
	}

	public Expression getField() {
		return field;
	}
	
	
	
	
}
