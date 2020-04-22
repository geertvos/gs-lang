package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.Compilable;
import net.geertvos.gvm.compiler.GCompiler;

public abstract class Statement implements Compilable {

	public abstract void compile( GCompiler c );
	
}
