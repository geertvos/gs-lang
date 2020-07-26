package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ArrayReferenceExpression extends Expression implements FieldReferenceExpression {

	private Expression index;
	private FieldReferenceExpression reference;

	public ArrayReferenceExpression(Expression index, FieldReferenceExpression reference) {
		this.index = index;
		this.reference = reference;
	}
	
	public FieldReferenceExpression getReference() {
		return reference;
	}
	
	public Expression getIndex() {
		return index;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		reference.compile(c);
		index.compile(c);
		c.code.add(GVM.GET);
	}

	@Override
	public FieldReferenceExpression setField(Expression e) {
		reference.setField(e);
		return this;
	}

	@Override
	public Expression getField() {
		return reference.getField();
	}

}
