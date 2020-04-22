package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.Compilable;
import net.geertvos.gvm.compiler.GCompiler;

public abstract class Expression implements Compilable {

	abstract public void compile(GCompiler c);

}
