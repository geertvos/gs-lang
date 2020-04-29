package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.Compilable;
import net.geertvos.gvm.compiler.GScriptCompiler;

public abstract class Statement implements Compilable {

	public abstract void compile( GScriptCompiler c );
	
}
