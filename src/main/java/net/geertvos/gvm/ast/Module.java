package net.geertvos.gvm.ast;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.geertvos.gvm.compiler.Compilable;
import net.geertvos.gvm.compiler.GScriptCompiler;

public class Module implements Scope, Compilable {

	private String name;
	private Set<String> imports = new HashSet<String>();
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

	public Module setName(String name) {
		this.name = name;
		return this;
	}
	
	public Module addImport(String name) {
		this.imports.add(name);
		return this;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Set<String> getImports() {
		return imports;
	}

	@Override
	public void compile(GScriptCompiler c) {
		
	}
	
}
