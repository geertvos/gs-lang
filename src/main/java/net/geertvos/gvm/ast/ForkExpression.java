package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ForkExpression  extends Expression {

	@Override
	public void compile(GScriptCompiler c) {
		c.code.write(GVM.FORK);
	}
	
}
