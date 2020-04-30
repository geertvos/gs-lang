package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class ImplicitConstructorExpression extends Expression implements Scope {

	private final List<Statement> statements = new LinkedList<Statement>();
	private Position pos;
	
	public ImplicitConstructorExpression(Position pos) {
		this.pos = pos;
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
	public void compile(GScriptCompiler c) {
		ConstantExpression constant = new ConstantExpression();
		constant.compile(c);  //Create new scope

		List<Statement> functionStatements = new LinkedList<Statement>(statements);
		functionStatements.add(new ReturnStatement(new ThisExpression(), pos));
		FunctionDefExpression functionDef = new FunctionDefExpression(new LinkedList<String>(), functionStatements);
		functionDef.compile(c);
		
		c.code.add(GVM.INVOKE); //Invoke the function on the stack
		c.code.writeInt(0); //No arguments
	}

}
