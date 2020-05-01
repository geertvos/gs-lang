package net.geertvos.gvm.ast;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.core.Value.TYPE;

public class ConstantExpression extends Expression {

	private final Value.TYPE type;
	private int value;
	private String string;
	
	public ConstantExpression( int value, Value.TYPE type )
	{
		this.value = value;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "ConstantExpression [type=" + type + ", value=" + value + ", string=" + string + "]";
	}

	public ConstantExpression( String s )
	{
		this.string = s;
		this.type = Value.TYPE.STRING;
	}

	public ConstantExpression()
	{
		this.type = TYPE.OBJECT;
	}	
	
	@Override
	public void compile(GScriptCompiler c) {
		if (type == Value.TYPE.BOOLEAN)
		{
			c.code.add( GVM.LDC_B);
			c.code.write( (byte)value );
		}
		else if (type == Value.TYPE.NUMBER) 
		{
			c.code.add( GVM.LDC_N);
			c.code.writeInt(value);
		}
		else if (type == Value.TYPE.FUNCTION) 
		{
			c.code.add( GVM.LDC_F);
			c.code.writeInt(value);
		}
		else if (type == Value.TYPE.STRING)
		{
			c.code.add( GVM.LDC_S);
			c.code.writeInt( c.getProgram().addString(string) );
			return;
		}
		else if (type == Value.TYPE.UNDEFINED)
		{
			c.code.add( GVM.LDC_U);
			return;
		}
		else if (type == Value.TYPE.OBJECT)
		{
			c.code.add( GVM.NEW);
			return;
		}
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public Value.TYPE getType() {
		return type;
	}
	
	
	
}
