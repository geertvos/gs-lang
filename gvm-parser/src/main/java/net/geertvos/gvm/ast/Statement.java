package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.Compilable;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public abstract class Statement implements Compilable {

	private final Position position;
	
	protected Statement(Position pos) {
		this.position = pos;
	}
	
	public void compile( GScriptCompiler c ) {
		if(c.isDebugModeEnabled()) {
			c.code.add(GVM.DEBUG);
			c.code.writeInt(position.line);
		}
	}
	
}
