package net.geertvos.gvm.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;
import net.geertvos.gvm.ast.Statement;
import net.geertvos.gvm.ast.AdditiveExpression;
import net.geertvos.gvm.ast.AndExpression;
import net.geertvos.gvm.ast.AssignmentExpression;
import net.geertvos.gvm.ast.ConditionalExpression;
import net.geertvos.gvm.ast.ConstantExpression;
import net.geertvos.gvm.ast.ConstructorExpression;
import net.geertvos.gvm.ast.EqualityExpression;
import net.geertvos.gvm.ast.ExpressionStatement;
import net.geertvos.gvm.ast.Expression;
import net.geertvos.gvm.ast.FieldReferenceExpression;
import net.geertvos.gvm.ast.ForStatement;
import net.geertvos.gvm.ast.FunctionCallExpression;
import net.geertvos.gvm.ast.FunctionDefExpression;
import net.geertvos.gvm.ast.ImplicitConstructorExpression;
import net.geertvos.gvm.ast.JumpStatement;
import net.geertvos.gvm.ast.NativeFunctionCallExpression;
import net.geertvos.gvm.ast.NotExpression;
import net.geertvos.gvm.ast.OrExpression;
import net.geertvos.gvm.ast.Parameterizable;
import net.geertvos.gvm.ast.MultiplicativeExpression;
import net.geertvos.gvm.ast.Program;
import net.geertvos.gvm.ast.VariableExpression;
import net.geertvos.gvm.ast.ReturnStatement;
import net.geertvos.gvm.ast.Scope;
import nl.gvm.core.Value;
import net.geertvos.gvm.ast.RelationalExpression;

@BuildParseTree
class Parser extends BaseParser<Object> {

	Rule Program() {
		return Sequence(push(new Program()), Statements());
	}

	Rule Statements() {
		Var<Scope> scopeVar = new Var<Scope>();
		return Sequence(scopeVar.set((Scope)pop()), Statement(), push(scopeVar.get().addStatement((Statement)pop())), ZeroOrMore(Sequence(SEMI, Statement())), ZeroOrMore(SEMI));
	}

	Rule Statement() {
		return FirstOf(ReturnValueStatement(),ReturnStatement(), ForStatement(), ExpressionStatement(), BreakStatement());
	}
	
	Rule ForStatement() {
		return Sequence(FOR,LBRACE,Statement(),SEMI,Expression(),SEMI,Statement(), RBRACE,push(new ForStatement((Statement)pop(),(Expression)pop(),(Statement)pop())), LCURLY, Statements() , RCURLY);
	}

	Rule ReturnValueStatement() {
		return Sequence(RETURN, Expression(), push(new ReturnStatement((Expression)pop())));
	}
	
	Rule ReturnStatement() {
		return Sequence(RETURN, push(new ReturnStatement()));
	}

	Rule ContinueStatement() {
		//TODO: To be implemented correctly
		return Sequence(CONTINUE, push(new JumpStatement()));
	}

	Rule BreakStatement() {
		//TODO: To be implemented correctly
		return Sequence(BREAK, push(new JumpStatement()));
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
                ZeroOrMore(Terminal("||"), ConditionalAndExpression(), push(new OrExpression((Expression)pop(), (Expression)pop())))
        );
    }
	
    Rule ConditionalAndExpression() {
        return Sequence(
        		EqualityExpression(),
                ZeroOrMore(Terminal("&&"), EqualityExpression(), push(new AndExpression((Expression)pop(), (Expression)pop())))
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
                OtherExpression()
        );
    }
    
    Rule OtherExpression() {
		return FirstOf(ObjectDefinition(), FunctionDefinition(), ConstructorCall(),
				NativeFunctionCall(), FunctionCall(), Number(), Boolean(), String(), Reference());
    }
    
	Rule Assignment() {
		return Sequence(Reference(), EQUALS, Expression(),
				push(new AssignmentExpression((Expression) pop(), (Expression) pop())));
	}
	
	Rule ObjectDefinition() {
		return Sequence(LCURLY, push(new ImplicitConstructorExpression()), ZeroOrMore(Statements()), RCURLY);
	}

	Rule ConstructorCall() {
		return Sequence(NEW, FunctionCall(), push(new ConstructorExpression((FunctionCallExpression) pop())));
	}

	Rule FunctionDefinition() {
		return Sequence(LBRACE,push(new FunctionDefExpression()), ZeroOrMore(ArgumentDefinition()), RBRACE, ARROW, LCURLY, ZeroOrMore(Statements()), RCURLY);
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
		return Sequence(FirstOf(FunctionCall(), Variable()), ZeroOrMore(SubReferences()));
	}

	Rule SubReferences() {
		Var<Expression> parentVar = new Var<Expression>();
		return Sequence(parentVar.set((Expression) pop()), DOT, Reference(), push( ((FieldReferenceExpression)pop()).setField(parentVar.get())));
	}

	Rule ArgumentDefinition() {
		return Sequence(Identifier(), push( ((FunctionDefExpression)pop()).addParameter(match())));
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
		return Sequence("\"", ZeroOrMore(FirstOf(CharRange('A', 'z'),AnyOf(".,!?@#$%&*()|:; "))), push(new ConstantExpression(match())), "\"");
	}

	@SuppressSubnodes
	Rule Identifier() {
		return Sequence(Letter(), ZeroOrMore(LetterOrDigit()), Spacing());
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
		return Sequence(OneOrMore(CharRange('0', '9')),
				push(new ConstantExpression(Integer.parseInt(match()), Value.TYPE.NUMBER)));
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
	final Rule IF = Terminal("if");
	final Rule BREAK = Terminal("break");
	final Rule CONTINUE = Terminal("continue");

	@SuppressNode
	@DontLabel
	Rule Terminal(String string) {
		return Sequence(string, Spacing()).label('\'' + string + '\'');
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