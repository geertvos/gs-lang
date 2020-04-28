package net.geertvos.gvm.parser;

import java.util.Stack;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import net.geertvos.gvm.ast.AdditiveExpression;
import net.geertvos.gvm.ast.AndExpression;
import net.geertvos.gvm.ast.AssignmentExpression;
import net.geertvos.gvm.ast.ConditionalExpression;
import net.geertvos.gvm.ast.ConstantExpression;
import net.geertvos.gvm.ast.ConstructorExpression;
import net.geertvos.gvm.ast.EqualityExpression;
import net.geertvos.gvm.ast.Expression;
import net.geertvos.gvm.ast.ExpressionStatement;
import net.geertvos.gvm.ast.FieldReferenceExpression;
import net.geertvos.gvm.ast.ForStatement;
import net.geertvos.gvm.ast.FunctionCallExpression;
import net.geertvos.gvm.ast.FunctionDefExpression;
import net.geertvos.gvm.ast.IfStatement;
import net.geertvos.gvm.ast.ImplicitConstructorExpression;
import net.geertvos.gvm.ast.JumpStatement;
import net.geertvos.gvm.ast.LoopStatement;
import net.geertvos.gvm.ast.MultiplicativeExpression;
import net.geertvos.gvm.ast.NativeFunctionCallExpression;
import net.geertvos.gvm.ast.NotExpression;
import net.geertvos.gvm.ast.OrExpression;
import net.geertvos.gvm.ast.Parameterizable;
import net.geertvos.gvm.ast.PostFixOperatorExpression;
import net.geertvos.gvm.ast.Program;
import net.geertvos.gvm.ast.RelationalExpression;
import net.geertvos.gvm.ast.ReturnStatement;
import net.geertvos.gvm.ast.Scope;
import net.geertvos.gvm.ast.ScopeStatement;
import net.geertvos.gvm.ast.Statement;
import net.geertvos.gvm.ast.ThisExpression;
import net.geertvos.gvm.ast.VariableExpression;
import net.geertvos.gvm.ast.WhileStatement;
import nl.gvm.core.Value;

@BuildParseTree
class Parser extends BaseParser<Object> {

	private Stack<JumpStatement> breakStack = new Stack<>();
	private Stack<JumpStatement> continueStack = new Stack<>();
	
	Rule Program() {
		return Sequence(push(new Program()), Statements());
	}

	Rule Statements() {
		Var<Scope> scopeVar = new Var<Scope>();
		return Sequence(scopeVar.set((Scope)pop()),Spacing(), Statement(), push(scopeVar.get().addStatement((Statement)pop())),
				        ZeroOrMore(Sequence(SEMI, scopeVar.set((Scope)pop()),Statement(), push(scopeVar.get().addStatement((Statement)pop())))), OneOrMore(SEMI));
	}

	
	Rule Statement() {
		return FirstOf(ReturnValueStatement(),ReturnStatement(), ForStatement(),WhileStatement(), IfStatement(), ExpressionStatement(), BreakStatement(), ContinueStatement(), ScopeStatement());
	}
	
	Rule ScopeStatement() {
		return Sequence(LCURLY, push(new ScopeStatement()), Statements(), RCURLY);
	}
	
	Rule ForStatement() {
		return Sequence(FOR,LBRACE,Expression(), SEMI, Expression() ,SEMI,Expression(), RBRACE, Statement(), pushLoop(new ForStatement((Statement)pop(), (Expression)pop(), (Expression)pop(), (Expression)pop())));
	}

	public boolean pushLoop(LoopStatement v) {
		for(JumpStatement jump : breakStack) {
			v.addBreak(jump);
		}
		for(JumpStatement jump : continueStack) {
			v.addContinue(jump);
		}
		this.push(v);
		return true;
	}
	
	public boolean pushBreak(JumpStatement v) {
		breakStack.push(v);
		this.push(v);
		return true;
	}
	
	public boolean pushContinue(JumpStatement v) {
		continueStack.push(v);
		this.push(v);
		return true;
	}

	Rule WhileStatement() {
		return Sequence(WHILE,LBRACE,Expression(), RBRACE, Statement(), pushLoop(new WhileStatement((Statement)pop(), (Expression)pop())));
	}

	Rule IfStatement() {
		return Sequence(IF,LBRACE,Expression(), RBRACE, Statement(), push(new IfStatement((Statement)pop(), (Expression)pop())));
	}

	Rule ReturnValueStatement() {
		return Sequence(RETURN, Expression(), push(new ReturnStatement((Expression)pop())));
	}
	
	Rule ReturnStatement() {
		return Sequence(RETURN, push(new ReturnStatement()));
	}

	Rule ContinueStatement() {
		//TODO: To be implemented correctly
		return Sequence(CONTINUE, pushContinue(new JumpStatement()));
	}

	Rule BreakStatement() {
		return Sequence(BREAK, pushBreak(new JumpStatement()));
	}

	
	Rule ExpressionStatement() {
		return Sequence(Expression(), push(new ExpressionStatement((Expression) pop())));
	}

	
    Rule Expression() {
        return Sequence(
                ConditionalExpression(),
                ZeroOrMore(AssignmentOperator(), ConditionalExpression(), push(new AssignmentExpression((Expression) pop(), (String)pop(), (Expression) pop())))
        );
    }

    Rule AssignmentOperator() {
        return Sequence(FirstOf(EQUALS, Terminal("+="), Terminal("-="), Terminal("*="), Terminal("/=")),push(match()));
    }

    Rule ConditionalExpression() {
        return Sequence(
                ConditionalOrExpression(),
                ZeroOrMore(QUESTION, Expression(), Terminal(":"), ConditionalOrExpression(), push(new ConditionalExpression((Expression)pop(), (Expression)pop(), (Expression)pop())))
        );
    }
	
    Rule ConditionalOrExpression() {
        return Sequence(
        		ConditionalAndExpression(),
                ZeroOrMore(OROR, ConditionalAndExpression(), push(new OrExpression((Expression)pop(), (Expression)pop())))
        );
    }
	
    Rule ConditionalAndExpression() {
        return Sequence(
        		EqualityExpression(),
                ZeroOrMore(ANDAND, EqualityExpression(), push(new AndExpression((Expression)pop(), (Expression)pop())))
        );
    }

//    Rule InclusiveOrExpression() {
//        return Sequence(
//                ExclusiveOrExpression(),
//                ZeroOrMore(OR, ExclusiveOrExpression())
//        );
//    }
//
//    Rule ExclusiveOrExpression() {
//        return Sequence(
//                AndExpression(),
//                ZeroOrMore(HAT, AndExpression())
//        );
//    }
//
//    Rule AndExpression() {
//        return Sequence(
//                EqualityExpression(),
//                ZeroOrMore(AND, EqualityExpression())
//        );
//    }

    Rule EqualityExpression() {
        return Sequence(
                RelationalExpression(),
                ZeroOrMore(Sequence(FirstOf(Terminal("=="), Terminal("!=")), push(match())), RelationalExpression(), push(new EqualityExpression((Expression)pop(), (String)pop(), (Expression)pop())))
        );
    }


    Rule RelationalExpression() {
        return Sequence(
        		AdditiveExpression(),
                ZeroOrMore(
                        Sequence(Sequence(FirstOf(Terminal("<="),Terminal(">="),Terminal("<"),Terminal(">")), push(match())), AdditiveExpression(), push(new RelationalExpression((Expression)pop(), (String)pop(), (Expression)pop())))));
    }
    
    Rule AdditiveExpression() {
        return Sequence(
                MultiplicativeExpression(),
                ZeroOrMore(Sequence(FirstOf(PLUS, MINUS),push(match()), MultiplicativeExpression(), push(new AdditiveExpression((Expression)pop(), (String)pop(), (Expression)pop()))))
        );
    }

    Rule MultiplicativeExpression() {
        return Sequence(
                UnaryExpression(),
                ZeroOrMore(Sequence(FirstOf(STAR, FWDSLASH, MOD),push(match()), UnaryExpression(), push(new MultiplicativeExpression((Expression)pop(), (String)pop(), (Expression)pop()))))
        );
    }
    
    Rule UnaryExpression() {
        return FirstOf(
                Sequence(EXCLAMATION, UnaryExpression(), push(new NotExpression((Expression)pop()))),
                Sequence(Reference(),PostFixOperator(), push(new PostFixOperatorExpression(match(), (Expression)(pop())))),
                OtherExpression()
        );
    }
    
    Rule PostFixOperator() {
    	return FirstOf(Terminal("++"),Terminal("--"));
    }
    
    Rule OtherExpression() {
		return FirstOf(ObjectDefinition(), FunctionDefinition(), ConstructorCall(),
				NativeFunctionCall(), FunctionCall(),Assignment(), Number(), Boolean(), String(), Reference());
    }
    
	Rule Assignment() {
		return Sequence(Reference(), EQUALS, Expression(),
				push(new AssignmentExpression((Expression) pop(), (Expression) pop())));
	}
	
	Rule ObjectDefinition() {
		return Sequence(NEW, LCURLY, push(new ImplicitConstructorExpression()), ZeroOrMore(Statements()), RCURLY);
	}

	Rule ConstructorCall() {
		return Sequence(NEW, FunctionCall(), push(new ConstructorExpression((FunctionCallExpression) pop())));
	}

	Rule FunctionDefinition() {
		return Sequence(LBRACE,push(new FunctionDefExpression()), ZeroOrMore(ArgumentDefinition(), ZeroOrMore(COMMA, ArgumentDefinition())), RBRACE, ARROW, LCURLY, ZeroOrMore(Statements()), RCURLY);
	}

	Rule NativeFunctionCall() {
		return Sequence("native", push(new NativeFunctionCallExpression()), LBRACE, FunctionArguments(), RBRACE);
	}

	Rule FunctionCall() {
		return Sequence(Variable(), push(new FunctionCallExpression((FieldReferenceExpression) pop())), LBRACE, FunctionArguments(), RBRACE);
	}
	
	Rule FunctionArguments() {
		return ZeroOrMore(Sequence(FunctionArgument(),ZeroOrMore(Sequence(COMMA, FunctionArgument()))));
	}

	Rule FunctionArgument() {
		Var<Expression> argumentVar = new Var<Expression>();
		return Sequence(Expression(), argumentVar.set((Expression)pop()), push(((Parameterizable)pop()).addParameter(argumentVar.get())));
	}

	Rule Reference() {
		return Sequence(FirstOf(FunctionCall(), SelfReference(), Variable()), ZeroOrMore(SubReferences()));
	}

	Rule SubReferences() {
		Var<Expression> parentVar = new Var<Expression>();
		return Sequence(parentVar.set((Expression) pop()), DOT, Reference(), push( ((FieldReferenceExpression)pop()).setField(parentVar.get())));
	}

	Rule ArgumentDefinition() {
		return Sequence(Identifier(), push( ((FunctionDefExpression)pop()).addParameter(match())));
	}

	Rule SelfReference() {
		return Sequence(THIS, push(new ThisExpression()));
	}
	
	Rule Variable() {
		return Sequence(Identifier(), push(new VariableExpression(match())));
	}

	Rule Boolean() {
		return FirstOf(
				Sequence(Terminal("true"), push(new ConstantExpression(1, Value.TYPE.BOOLEAN))),
				Sequence(Terminal("false"), push(new ConstantExpression(0, Value.TYPE.BOOLEAN)))
				);
	}
	
	@MemoMismatches
	Rule String() {
		//TODO: Fix and support UTF-8 strings 
		return Sequence("\"", ZeroOrMore(FirstOf(CharRange('A', 'z'),CharRange('0','9'),AnyOf(".,!?@#$%&*()|:; '<>"))), push(new ConstantExpression(match())), "\"");
	}

	@SuppressSubnodes
	Rule Identifier() {
		return Sequence(TestNot(ReservedKeywords()), Letter(), ZeroOrMore(LetterOrDigit()), Spacing());
	}

	Rule ReservedKeywords() {
		return FirstOf(QUESTION,EXCLAMATION,NEW,NATIVE,THIS, RETURN, BREAK,IF,WHILE,FOR,CONTINUE);
	}

	
	@MemoMismatches
	Rule Letter() {
		return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '_', '$');
	}

	@MemoMismatches
	Rule LetterOrDigit() {
		return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$');
	}

	@MemoMismatches
	Rule Number() {
		return Sequence(OneOrMore(CharRange('0', '9')),	push(new ConstantExpression(Integer.parseInt(match().trim()), Value.TYPE.NUMBER)));
	}

	final Rule DOT = Terminal(".");
	final Rule EXCLAMATION = Terminal("!");
	final Rule QUESTION = Terminal("?");
	final Rule COMMA = Terminal(",");
	final Rule SEMI = Terminal(";");
	final Rule EQUALS = Terminal("=");
	final Rule LBRACE = Terminal("(");
	final Rule RBRACE = Terminal(")");
	final Rule ARROW = Terminal("->");
	final Rule LCURLY = Terminal("{");
	final Rule RCURLY = Terminal("}");
	final Rule RETURN = Terminal("return");
	final Rule PLUS = Terminal("+");
	final Rule MINUS = Terminal("-");
	final Rule STAR = Terminal("*");
	final Rule FWDSLASH = Terminal("/");
	final Rule MOD = Terminal("%");
	final Rule NEW = Terminal("new");
	final Rule FOR = Terminal("for");
	final Rule WHILE = Terminal("while");
	final Rule DO = Terminal("do");
	final Rule IF = Terminal("if");
	final Rule BREAK = Terminal("break");
	final Rule CONTINUE = Terminal("continue");
	final Rule THIS = Terminal("this"); 
	final Rule NATIVE = Terminal("native"); 
	final Rule ANDAND = Terminal("&&"); 
	final Rule OROR = Terminal("||"); 

	@SuppressNode
	@DontLabel
	Rule Terminal(String string) {
		return Sequence(Spacing(), string, Spacing()).label('\'' + string + '\'');
	}

	@SuppressNode
	Rule Spacing() {
		return ZeroOrMore(FirstOf(

				// whitespace
				OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),

				// traditional comment
				Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),

				// end of line comment
				Sequence("//", ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), FirstOf("\r\n", '\r', '\n', EOI))));
	}
}