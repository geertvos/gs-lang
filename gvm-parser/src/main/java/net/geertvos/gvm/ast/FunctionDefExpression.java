package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.compiler.GCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.program.GVMFunction;
import net.geertvos.gvm.streams.RandomAccessByteStream;

public class FunctionDefExpression extends Expression implements Scope {

	private final List<Statement> statements;
	private final List<String> parameters;
	
	public FunctionDefExpression( List<String> parameters , List<Statement> stats )
	{
		this.statements = stats;
		this.parameters = parameters;
	}
	
	public FunctionDefExpression()
	{
		this.statements = new LinkedList<Statement>();
		this.parameters = new LinkedList<String>();
	}
	
	public Scope addStatement(Statement statement)
	{
		this.statements.add(statement);
		return this;
	}
	
	public FunctionDefExpression addParameter(String parameter) {
		this.parameters.add(parameter);
		return this;
	}
	
	public String getParameter(int index) {
		return parameters.get(index);
	}
	
	public Statement getStatement(int index) {
		return statements.get(index);
	}

	@Override
	public void compile(GCompiler c) {
		RandomAccessByteStream code = c.code;
		GVMFunction prev = c.getFunction();
		
		RandomAccessByteStream fcode = new RandomAccessByteStream();
		GVMFunction function = new GVMFunction( fcode, parameters);
		c.setFunction(function);
		c.code = fcode;
		
		int index = c.getProgram().addFunction(function);
		for( Statement s : statements )
		{
			s.compile(c);
		}
		c.code = code;
		c.setFunction(prev);
		c.code.add( GVM.LDC_F );
		c.code.writeInt( index );
		
	}

	public int getStatements() {
		return statements.size();
	}

}
