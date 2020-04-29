package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class ScopeStatement extends Statement implements Scope {

	private List<Statement> statements = new LinkedList<>();
	
	@Override
	public Scope addStatement(Statement statement) {
		statements.add(statement);
		return this;
	}

	@Override
	public Statement getStatement(int index) {
		return statements.get(index);
	}

	@Override
	public int getStatements() {
		return statements.size();
	}

	@Override
	public void compile(GScriptCompiler c) {
		for(Statement s : statements) {
			s.compile(c);
		}
	}

}
