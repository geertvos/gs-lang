package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.Compilable;

public interface FieldReferenceExpression extends Compilable {
	public FieldReferenceExpression setField( Expression e );
}
