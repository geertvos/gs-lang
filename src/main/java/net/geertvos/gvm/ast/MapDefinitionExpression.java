package net.geertvos.gvm.ast;

import java.util.LinkedList;
import java.util.List;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.lang.types.ObjectType;

public class MapDefinitionExpression extends Expression {

	private final List<Expression> keys = new LinkedList<Expression>();
	private final List<Expression> values = new LinkedList<Expression>();
	
	public MapDefinitionExpression() {
	}

	public MapDefinitionExpression addKeyValue(Expression key, Expression value) {
		keys.add(key);
		values.add(value);
		return this;
	}

	@Override
	public void compile(GScriptCompiler c) {
		c.code.add(GVM.NEW);
		c.code.writeString(new ObjectType().getName());
		for(int i=0;i<keys.size();i++) {
			values.get(i).compile(c);
			c.code.add(GVM.LDS);
			c.code.writeInt(-1);
			keys.get(i).compile(c);
			c.code.add(GVM.GET);
			c.code.add(GVM.PUT);
			c.code.add(GVM.POP);
		}
	}

}
