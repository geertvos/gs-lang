package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.FunctionType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.Undefined;
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
	public void compile(GScriptCompiler c) {
		RandomAccessByteStream code = c.code;
		GVMFunction prev = c.getFunction();
		
		RandomAccessByteStream functionCode = new RandomAccessByteStream();
		GVMFunction function = new GVMFunction( functionCode, parameters);
		c.setFunction(function);
		c.code = functionCode;
		
		int index = c.getProgram().addFunction(function);
		function.setIndex(index);
		
		for( Statement s : statements )
		{
				s.compile(c);
		}
		if(statements.isEmpty() || !(statements.get(statements.size()-1) instanceof ReturnStatement)) {
			c.code.add(GVM.LDC_D);
			c.code.writeInt(0);
			c.code.writeString(new Undefined().getName());
			c.code.add(GVM.RETURN);
		}
		c.code = code;
		c.setFunction(prev);
		c.code.add( GVM.LDC_D );
		c.code.writeInt( index );
		c.code.writeString(new FunctionType().getName());
		
	}


	public int getStatements() {
		return statements.size();
	}

}
