package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.Compilable;

public interface FieldReferenceExpression extends Compilable {
	
	FieldReferenceExpression setField( Expression e );
	
	Expression getField(); 
}
