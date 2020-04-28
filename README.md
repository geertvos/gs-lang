# gvm-parser
Parser and compiler to translate GScript in to the GVM bytecode language.

# Language
The GScript language is a basic functional scripting language loosely based on Javascript. It is both object oriented and functional where both Objects and Functions are first class citizens. 

# Types
At the core of GScript are 6 types:

 - Boolean
 - Number
 - String
 - Object
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

# Functions
The following example shows how to create a Constructor that creates person objects. 
```
person = (name) -> {
	this.name = name;
	getName = () -> { return this.name; };
	return this;
};
Person john = new Person("John");
```
# Anonymous objects
```
create = () -> {
	return new { 
		this.name = "MyName"; 
	};
};

```

# Native code support. 
This compiler compiles the GScript code in to an a bytecode format that can be executed by the GVM. (See other project). Both have support for 'native' code by binding a function to a Java method.

```
myFunction = (argument) -> {
	native("net.geertvos.gvm.ClassName", "MethodName", argument);
	return; 
};
```
