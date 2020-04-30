package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.GVM;

public class TryCatchBlock extends Statement {

	private final Statement tryBlock;
	private final Statement catchBlock;
	private final String variableName;
	
	public TryCatchBlock( Statement catchBlock , String variableName , Statement tryBlock, Position pos ) {
		super(pos);
		this.tryBlock = tryBlock;
		this.variableName = variableName;
		this.catchBlock = catchBlock;
	}
	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		int startIndex = c.code.getPointerPosition();
		tryBlock.compile(c);
		c.code.add(GVM.JMP);
		int endofTry = c.code.getPointerPosition();
		c.code.writeInt(0);//Set to end of catch
		c.getFunction().registerLocalVariable(variableName);
		c.code.add(GVM.LDS);
		c.code.writeInt(1+c.getFunction().getParameters().size()+c.getFunction().getLocals().indexOf(variableName));
		c.code.add(GVM.PUT);
		catchBlock.compile(c);
		//c.code.add(GVM.RETURN);
		c.code.set(endofTry, c.code.getPointerPosition());
		c.getFunction().registerCatchBlock(startIndex, endofTry-1, endofTry+4);
	}

}
