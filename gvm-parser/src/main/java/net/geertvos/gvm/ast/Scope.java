package net.geertvos.gvm.ast;

public interface Scope {

	public Scope addStatement(Statement statement);

	public Statement getStatement(int index);

	public int getStatements();
	
}
