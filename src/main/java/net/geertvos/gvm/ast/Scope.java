package net.geertvos.gvm.ast;

public interface Scope {

	Scope addStatement(Statement statement);

	Statement getStatement(int index);

	int getStatements();
	
}
