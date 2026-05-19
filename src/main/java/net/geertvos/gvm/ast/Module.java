package net.geertvos.gvm.ast;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.Compilable;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class Module implements Scope, Compilable {

	private final String name;
	private final Set<String> imports = new HashSet<String>();
	private final List<Statement> statements = new LinkedList<Statement>();
	private final Position pos;

	public Module(String name, Position pos) {
		this.pos = pos;
		this.name = name;
	}
	
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
		/**
		 * Create a new Object that will capture this module
		 */
		ConstantExpression constant = new ConstantExpression();
		constant.compile(c);

		/*
		 * Create the constructor for this module
		 */
		List<Statement> functionStatements = new LinkedList<Statement>(statements);
		functionStatements.add(new ReturnStatement(new ThisExpression(), pos));
		FunctionDefExpression functionDef = new FunctionDefExpression(new LinkedList<String>(), functionStatements);
		functionDef.compile(c);

		/**
		 * Execute the constructor and leave the object on the stack
		 */
		c.code.add(GVM.INVOKE); //Invoke the function on the stack
		c.code.writeInt(0); //No arguments

		/**
		 * Assign the Object that represents the module to the variable name
		 */
		Expression variable = new VariableExpression(name);
		variable.compile(c);

		c.code.add(GVM.PUT);

		/**
		 * Export module members as top-level variables so they can be
		 * used without the module prefix (e.g. BufferedReader instead of Net.BufferedReader)
		 */
		for (String exportedName : getExportedNames()) {
			// Push value: module.field
			new VariableExpression(exportedName, new VariableExpression(name)).compile(c);
			// Push variable target
			new VariableExpression(exportedName).compile(c);
			c.code.add(GVM.PUT);
			c.code.add(GVM.POP);
		}
	}

	private List<String> getExportedNames() {
		List<String> names = new LinkedList<>();
		for (Statement stmt : statements) {
			if (stmt instanceof ExpressionStatement) {
				Expression expr = ((ExpressionStatement) stmt).getExpression();
				if (expr instanceof AssignmentExpression) {
					Expression var = ((AssignmentExpression) expr).getVariable();
					if (var instanceof VariableExpression) {
						VariableExpression ve = (VariableExpression) var;
						if (ve.getField() == null && !names.contains(ve.getName())) {
							names.add(ve.getName());
						}
					}
				}
			}
		}
		return names;
	}
	
}
