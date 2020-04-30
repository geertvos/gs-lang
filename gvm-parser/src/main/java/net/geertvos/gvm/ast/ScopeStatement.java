package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;

public class ScopeStatement extends Statement implements Scope {

	public ScopeStatement(Position pos) {
		super(pos);
	}

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
		super.compile(c);
		for(Statement s : statements) {
			s.compile(c);
		}
	}

}
