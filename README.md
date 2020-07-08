# gs-lang 
Parser and compiler to translate GScript in to the GVM bytecode language. It depends on the gs-code module that implements the bytecode and vm. See https://github.com/geertvos/gs-core

# design goals
The idea behind the scripting language and the vm is that it easy to understand and modify, targeted for educational purposes. The entire bytecode language, the stack machine and the compiler and AST are easily readable, not optimized in any way and allow for easy modification. Therefor making it perferct for programming language courses etc.

# state of the project
Currently most of the basic language features are working. However, programming in the language is not yet very easy due to the lack of debug information. Also some of the open bugs make it hard to write anything useful yet. However, it already serves the main purposes!

# language
The GScript language is a basic functional scripting language loosely based on Javascript. It is both object oriented and functional where both Objects and Functions are first class citizens. 

# types
At the core of GScript are 7 types:

 - Boolean
 - Number
 - String
 - Object
 - Array
 - Function
 - Undefined 
 
A boolean can be defined by the constants true or false or as the results of an expression.
```
true;
false;
true || false;
true && false;
```

Strings are UTF-8 encoded and can be defined by using the following notation:
```
"This is a string"
```
For objects and functions, see the sections below. Undefined cannot be defined. It is the opposite.

# array support
The language supports arrays with dynamic lengths. 
```
names = new ["Johny", "Calvin", "Clara"];
System.print(names[0]);
```

# functions
The following example shows how to create a Constructor that creates person objects. 
```
person = (name) -> {
	this.name = name;
	getName = () -> { return this.name; };
	return this;
};
Person john = new Person("John");
```
# anonymous objects
```
create = () -> {
	return new { 
		this.name = "MyName"; 
	}
}

```

# control flow

The language has support for the usual control flow statements like the if, while and for loop. Both while and for support break and continue and work as expected.
```
if( a < b ) {
	print("a < b)
}

for(a=0;a<10;a++) {
	print("a: "+a)
}

while(a<100) {
	a++;
	print("a: "+a)
}
```

# exception handling
The language and underlying VM have support for exception handling. Any type can be thrown as an Exception. The VM internally will throw String objects with a message.
```
try {
	a = true + 5;
	print("Not good");
	throw "Should have been an exception already.";
} catch(a) {
	print("Exception caught: "+a);
};
```
# module support
All the code written in gscript is in scope of a module. With the import statement, other modules can be loaded. Each module is executed at load time. The example below creates Math module with a sum function in it. 
```
module Math;

sum = (a,b) -> {
	return a+b;
}
```
The function can be referenced from other modules:
```
module MathUser;
import Math;

value = Math.sum(2, 4)
```

# native code support. 
This compiler compiles the GScript code in to an a bytecode format that can be executed by the GVM. (See other project). Both have support for 'native' code by binding a function to a Java method.

```
myFunction = (argument) -> {
	native("net.geertvos.gvm.ClassName", "MethodName", argument);
	return; 
}
```

# tail recursion
The compiler contains support to optimze tail recursion. This will reduce the stack usage and will reuse the existing stackframe for the tail call. The optimization can be enabled/disabled. The optimization is implemented as a runtime check becuase the language is dynamically typed and functions are first class citizens. Only at runtime we can verify that we are performing an actual tail call.  The runtime check might impact performane.
