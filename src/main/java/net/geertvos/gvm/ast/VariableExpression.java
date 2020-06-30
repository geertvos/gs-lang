package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.lang.types.StringType;

public class VariableExpression extends Expression implements FieldReferenceExpression {

	private final String name;
	private Expression field;

	/**
	 * Construct a variable pointing to this.<name> Non existing fields will be
	 * created
	 * 
	 * @param name The name of the variable
	 */
	public VariableExpression(String name) {
		this.name = name.trim();
	}

	/**
	 * Construct a variable <field>.name Non existing fields will be created
	 * 
	 * @param name  The name of the variable
	 * @param field The field to which the variable belongs
	 */
	public VariableExpression(String name, Expression field) {
		this(name);
		this.field = field;
	}

	public FieldReferenceExpression setField(Expression field) {
		if (this.field != null)
			((FieldReferenceExpression) this.field).setField(field);
		else
			this.field = field;
		return this;
	}

	public void compile(GScriptCompiler c) {
		if (c.getFunction().getParameters().contains(name) && field == null) {
			// Variable points to a parameter
			c.code.add(GVM.LDS);
			c.code.writeInt(1 + c.getFunction().getParameters().indexOf(name));
		} else if (c.getFunction().getLocals().contains(name) && field == null) {
			// Variable points to a local variable
			c.code.add(GVM.LDS);
			c.code.writeInt(1 + c.getFunction().getParameters().size() + c.getFunction().getLocals().indexOf(name));
		} else if (field != null) {
			// Variable points to a field of the specified parent field
			field.compile(c);
			int ref = c.getProgram().addString(name);
			c.code.add(GVM.LDC_D);
			c.code.writeInt(ref);
			c.code.writeString(new StringType().getName());
			c.code.add(GVM.GET);
		} else {
			// It just refers to a field
			int ref = c.getProgram().addString(name); 
			c.code.add(GVM.LDC_D);
			c.code.writeInt(ref);
			c.code.writeString(new StringType().getName());
			c.code.add(GVM.GETDYNAMIC);
			return;
		}
	}

	public String getName() {
		return name;
	}

	public Expression getField() {
		return field;
	}

}
