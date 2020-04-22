package net.geertvos.gvm.parser;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import net.geertvos.gvm.ast.AdditiveExpression;
import net.geertvos.gvm.ast.AndExpression;
import net.geertvos.gvm.ast.AssignmentExpression;
import net.geertvos.gvm.ast.ConditionalExpression;
import net.geertvos.gvm.ast.ConstantExpression;
import net.geertvos.gvm.ast.ConstructorExpression;
import net.geertvos.gvm.ast.EqualityExpression;
import net.geertvos.gvm.ast.ExpressionStatement;
import net.geertvos.gvm.ast.FunctionCallExpression;
import net.geertvos.gvm.ast.FunctionDefExpression;
import net.geertvos.gvm.ast.ImplicitConstructorExpression;
import net.geertvos.gvm.ast.MultiplicativeExpression;
import net.geertvos.gvm.ast.NativeFunctionCallExpression;
import net.geertvos.gvm.ast.OrExpression;
import net.geertvos.gvm.ast.Program;
import net.geertvos.gvm.ast.RelationalExpression;
import net.geertvos.gvm.ast.ReturnStatement;
import net.geertvos.gvm.ast.VariableExpression;
import nl.gvm.compiler.GCompiler;
import nl.gvm.core.GVM;
import nl.gvm.core.Value;
import nl.gvm.program.GVMProgram;

public class ParserTest {

	@Test()
	public void testIntegerAssignment() {
		String assignment = "a = 10";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		AssignmentExpression assignmentExpression = (AssignmentExpression) statement.getExpression();
		VariableExpression variable = (VariableExpression) assignmentExpression.getVariable();
		ConstantExpression value = (ConstantExpression)assignmentExpression.getValue();  

		assertEquals(variable.getName(), "a");
		assertEquals(value.getType(), Value.TYPE.NUMBER);
		assertEquals(value.getValue(), 10);
	}

	@Test()
	public void testIntegerIncrement() {
		String assignment = "a += 10";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		AssignmentExpression assignmentExpression = (AssignmentExpression) statement.getExpression();
		VariableExpression variable = (VariableExpression) assignmentExpression.getVariable();
		ConstantExpression value = (ConstantExpression)assignmentExpression.getValue();  

		assertEquals(variable.getName(), "a");
		assertEquals(value.getType(), Value.TYPE.NUMBER);
		assertEquals(value.getValue(), 10);
		assertEquals("+=", assignmentExpression.getOperator());
	}

	@Test()
	public void testConditional() {
		String assignment = "a?b:c";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		ConditionalExpression conditionalExpression = (ConditionalExpression) statement.getExpression();
		VariableExpression condition = (VariableExpression) conditionalExpression.getCondition();
		VariableExpression positive = (VariableExpression) conditionalExpression.getPositiveExpression();
		VariableExpression negative = (VariableExpression) conditionalExpression.getNegativeExpression();
		assertEquals(condition.getName(), "a");
		assertEquals(positive.getName(), "b");
		assertEquals(negative.getName(), "c");
	}

	@Test()
	public void testBooleanTrue() {
		String assignment = "true";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		ConstantExpression expression = (ConstantExpression) statement.getExpression();
		assertEquals(expression.getType(), Value.TYPE.BOOLEAN);
		assertEquals(expression.getValue(), 1);
	}

	@Test()
	public void testBooleanFalse() {
		String assignment = "false";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		ConstantExpression expression = (ConstantExpression) statement.getExpression();
		assertEquals(expression.getType(), Value.TYPE.BOOLEAN);
		assertEquals(expression.getValue(), 0);
	}

	@Test()
	public void testOR() {
		String assignment = "a || b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		OrExpression conditionalExpression = (OrExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) conditionalExpression.getLhs();
		VariableExpression rhs = (VariableExpression) conditionalExpression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
	}

	@Test()
	public void testAND() {
		String assignment = "a && b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		AndExpression conditionalExpression = (AndExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) conditionalExpression.getLhs();
		VariableExpression rhs = (VariableExpression) conditionalExpression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
	}
	
	@Test()
	public void testEquality() {
		String assignment = "a == b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		EqualityExpression expression = (EqualityExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals("==", expression.getOperator());
	}

	@Test()
	public void testRelational() {
		String assignment = "a <= b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		RelationalExpression expression = (RelationalExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals("<=", expression.getOperator());
	}
	
	@Test()
	public void testRelationalGreaterEquals() {
		String assignment = "a >= b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		RelationalExpression expression = (RelationalExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals(">=", expression.getOperator());
	}
	
	@Test()
	public void testAdditive() {
		String assignment = "a + b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		AdditiveExpression expression = (AdditiveExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals("+", expression.getOperator());
	}
	
	@Test()
	public void testAdditiveMinus() {
		String assignment = "a - b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		AdditiveExpression expression = (AdditiveExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals("-", expression.getOperator());
	}
	
	@Test()
	public void testMultiply() {
		String assignment = "a * b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		MultiplicativeExpression expression = (MultiplicativeExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals("*", expression.getOperator());
	}
	
	@Test()
	public void testDivide() {
		String assignment = "a / b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		MultiplicativeExpression expression = (MultiplicativeExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals("/", expression.getOperator());
	}
	
	@Test()
	public void testModulus() {
		String assignment = "a % b";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		MultiplicativeExpression expression = (MultiplicativeExpression) statement.getExpression();
		VariableExpression lhs = (VariableExpression) expression.getLhs();
		VariableExpression rhs = (VariableExpression) expression.getRhs();
		assertEquals(lhs.getName(), "a");
		assertEquals(rhs.getName(), "b");
		assertEquals("%", expression.getOperator());
	}
	
	@Test()
	public void testStringAssignment() {
		String assignment = "a = \"b\"";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		AssignmentExpression assignmentExpression = (AssignmentExpression) statement.getExpression();
		VariableExpression variable = (VariableExpression) assignmentExpression.getVariable();
		ConstantExpression value = (ConstantExpression)assignmentExpression.getValue();  

		assertEquals(variable.getName(), "a");
		assertEquals(value.getType(), Value.TYPE.STRING);
		assertEquals(value.getString(), "b");
	}

	@Test()
	public void testFunctionDef() {
		String assignment = "(a)->{ return b; }";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionDefExpression functionDefExpression = (FunctionDefExpression) statement.getExpression();
		assertEquals("a", functionDefExpression.getParameter(0));
		ReturnStatement returnStatement = (ReturnStatement) functionDefExpression.getStatement(0);
		VariableExpression variable = (VariableExpression) returnStatement.getExpression(0);
		assertEquals("b", variable.getName());
	}

	@Test()
	public void testFunctionCall() {
		String assignment = "a();";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) statement.getExpression();
		VariableExpression field = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("a", field.getName());
	}

	@Test()
	public void testNativeFunctionCall() {
		String assignment = "native(\"nl.gvm.main.GVMNatives\",\"printStdOut\",\"This demo is alive!\");";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		NativeFunctionCallExpression functionCallExpression = (NativeFunctionCallExpression) statement.getExpression();
	}

	@Test()
	public void testFunctionCallOnObject() {
		String assignment = "person.getName();";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) statement.getExpression();
		VariableExpression field = (VariableExpression) functionCallExpression.getField();
		assertEquals("person", field.getName());
		VariableExpression function = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("getName", function.getName());
	}

	@Test()
	public void testFunctionCallOnObjectArguments() {
		String assignment = "person.getName(a,b);";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) statement.getExpression();
		VariableExpression field = (VariableExpression) functionCallExpression.getField();
		assertEquals("person", field.getName());
		VariableExpression function = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("getName", function.getName());
		
		assertEquals(2, functionCallExpression.getParameterCount());
		VariableExpression parameter1 = (VariableExpression) functionCallExpression.getParameter(0);
		assertEquals("a", parameter1.getName());
		VariableExpression parameter2 = (VariableExpression) functionCallExpression.getParameter(1);
		assertEquals("b", parameter2.getName());

	}

	@Test()
	public void testFunctionCallArgument() {
		String assignment = "a(b);";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) statement.getExpression();
		VariableExpression field = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("a", field.getName());
		assertEquals(1, functionCallExpression.getParameterCount());
		VariableExpression parameter = (VariableExpression) functionCallExpression.getParameter(0);
		assertEquals("b", parameter.getName());
	}


	@Test()
	public void testFunctionCallStringArgument() {
		String assignment = "a(\"b\");";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) statement.getExpression();
		VariableExpression field = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("a", field.getName());
		assertEquals(1, functionCallExpression.getParameterCount());
		ConstantExpression parameter = (ConstantExpression) functionCallExpression.getParameter(0);
		assertEquals("b", parameter.getString());
	}

	@Test()
	public void testFunctionCallFunctionArgument() {
		String assignment = "a( ()->{} );";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) statement.getExpression();
		VariableExpression field = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("a", field.getName());
		assertEquals(1, functionCallExpression.getParameterCount());
		FunctionDefExpression parameter = (FunctionDefExpression) functionCallExpression.getParameter(0);
		assertEquals(0, parameter.getStatements());
	}


	
	@Test()
	public void testFunctionCallArguments() {
		String assignment = "a(b,c);";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) statement.getExpression();
		VariableExpression field = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("a", field.getName());
		assertEquals(2, functionCallExpression.getParameterCount());
		VariableExpression parameter1 = (VariableExpression) functionCallExpression.getParameter(0);
		assertEquals("b", parameter1.getName());
		VariableExpression parameter2 = (VariableExpression) functionCallExpression.getParameter(1);
		assertEquals("c", parameter2.getName());
	}

	@Test()
	public void testConstructorCall() {
		String assignment = "new a(b);";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		ConstructorExpression constructorCallExpression = (ConstructorExpression) statement.getExpression();
		FunctionCallExpression functionCallExpression = (FunctionCallExpression) constructorCallExpression.getFunction();
		VariableExpression field = (VariableExpression) functionCallExpression.getFunction();
		assertEquals("a", field.getName());
		assertEquals(1, functionCallExpression.getParameterCount());
		VariableExpression parameter = (VariableExpression) functionCallExpression.getParameter(0);
		assertEquals("b", parameter.getName());
	}

	@Test()
	public void testImplicitConstructor() {
		String assignment = "{}";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		ImplicitConstructorExpression implicitConstructor = (ImplicitConstructorExpression)statement.getExpression();
		assertEquals(0, implicitConstructor.getStatements());
	}

	@Test()
	public void testImplicitConstructorWithStatements() {
		String assignment = "{ print(\"a\"); }";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		ImplicitConstructorExpression implicitConstructor = (ImplicitConstructorExpression)statement.getExpression();
		assertEquals(1, implicitConstructor.getStatements());
	}

	@Test()
	public void testForLoop() {
		String assignment = "for(i=0;i;i=1) {} ;";
		Program program = (Program) parse(assignment);
		ExpressionStatement statement = (ExpressionStatement) program.getStatement(0);
		//TODO: implement test
	}

	
	public Object parse(String code) {
		Parser parser = Parboiled.createParser(Parser.class);
		ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Program()).run(code);
		if (!result.parseErrors.isEmpty()) {
			System.out.println(ErrorUtils.printParseError(result.parseErrors.get(0)));
			Assert.fail(ErrorUtils.printParseError(result.parseErrors.get(0)));
		} else {
			String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
			System.out.println(parseTreePrintOut);
		}
		Object value = result.parseTreeRoot.getValue();
		return value;
	}
	
}
