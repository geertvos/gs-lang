# gvm-parser
Parser and compiler to translate GScript in to the GVM bytecode language.

# Language
The GScript language is a basic functional scripting language loosely based on Javascript. It is both object oriented and functional where both Objects and Functions are first class citizens. 

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
