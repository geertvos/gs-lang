package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;

public class ImplicitConstructorExpression extends Expression implements Scope {

	private final List<Statement> statements = new LinkedList<Statement>();

	public ImplicitConstructorExpression() {
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
	
	@Override
	public void compile(GCompiler c) {
		ConstantExpression constant = new ConstantExpression();
		constant.compile(c);  //Create new scope

		List<Statement> functionStatements = new LinkedList<Statement>(statements);
		functionStatements.add(new ReturnStatement(new ThisExpression()));
		FunctionDefExpression functionDef = new FunctionDefExpression(new LinkedList<String>(), functionStatements);
		functionDef.compile(c);
		
		c.code.add(GVM.INVOKE); //Invoke the function on the stack
		c.code.writeInt(0); //No arguments
	}

}
