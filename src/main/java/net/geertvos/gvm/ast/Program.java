package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

public class Program implements Scope {

	private final List<Statement> statements = new LinkedList<Statement>();

	public Scope addStatement(Statement statement) {
		statements.add(statement);
		return this;
	}

	public Statement getStatement(int index) {
		return statements.get(index);
	}

	public int getStatements() {
		return statements.size();
	}
	
	public List<Statement> getAll() {
		return statements;
	}

}
