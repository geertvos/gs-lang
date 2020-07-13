package net.geertvos.gvm.ast;

import org.parboiled.support.Position;

import net.geertvos.gvm.compiler.CompilerOptimizations;
import net.geertvos.gvm.compiler.GScriptCompiler;
import net.geertvos.gvm.core.FunctionType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.Undefined;

public class ReturnStatement extends Statement {

	private final Expression returnValue;
	
	public ReturnStatement(Position pos)
	{
		this(null, pos);
	}

	public ReturnStatement( Expression val, Position pos )
	{
		super(pos);
		this.returnValue = val;
	}

	
	@Override
	public void compile(GScriptCompiler c) {
		super.compile(c);
		if( returnValue == null )  {
			c.code.add(GVM.LDC_D);
			c.code.writeInt(0);
			c.code.writeString(new Undefined().getName());
		}
		else {
			if(canOptimizeTailRecursion(c)) {
				//We have a function call, compare to this function
				generateRuntimeChecks(c);
				returnValue.compile(c);
			} else {
				returnValue.compile(c);
			}
		}
		c.code.add(GVM.RETURN);
	}

	private void generateRuntimeChecks(GScriptCompiler c) {
		/**
		 * This code generate a check where the function called as return variable is compared
		 * to the current function. If they match, we use tail recursion to optimize stack usage.
		 * 
		 * If the comparison fails, we just jump over the code that resets the parameters and return.
		 * If the comparison is true, we update the parameter values as if we called the function,
		 * and then jump to the start again of this function. Skipping a stack allocation.
		 */
		FunctionCallExpression fc = (FunctionCallExpression)returnValue;
		FieldReferenceExpression field = fc.getFunction();
		field.compile(c);
		c.code.add(GVM.LDC_D);
		c.code.writeInt(c.getFunction().getIndex()); 
		c.code.writeString(new FunctionType().getName());
		c.code.add(GVM.EQL);
		c.code.add(GVM.NOT);
		c.code.add(GVM.CJMP);   //We check if we are calling the same function. If not,  jump to return.
		int jumpLocation = c.code.getPointerPosition();
		c.code.writeInt(-1);
		//Update the parameter values instead of calling the function
		for(int x=0 ; x<fc.getParameterCount() ; x++) {
			fc.getParameter(x).compile(c);
			c.code.add(GVM.LDS);
			c.code.writeInt(1 + x);
			c.code.add(GVM.PUT);
			c.code.add(GVM.POP);
		}
		c.code.add(GVM.JMP); //Do not continue with return, but jump to beginning of function again.
		c.code.writeInt(0);
		int jump = c.code.getPointerPosition();
		c.code.set(jumpLocation, jump);
	}

	private boolean canOptimizeTailRecursion(GScriptCompiler compiler) {
		return returnValue instanceof FunctionCallExpression && compiler.isEnabled(CompilerOptimizations.TAIL_RECURSION);
	}
	
	public Expression getReturnValue() {
		return returnValue;
	}

}
