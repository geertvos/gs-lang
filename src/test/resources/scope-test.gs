module ScopeTest;
import System;

assertStringEquals = (value, expected) -> {
	test = value==expected;
	System.print(" >> Testing value: '"+value+"' against: '" +expected+"', should be '"+test+"'");
	native("net.geertvos.gvm.parser.GVMIntegrationTest", "testEqualsString", value ,expected);
};
System.print("Starting integration test.");

person = (name) -> {
	this.name = name;
	getName = () -> { return this.name }
	return this
}

System.print("Object and function scope test");

geert = new person("Geert"); 
System.print(geert.getName());

assertStringEquals(geert.getName(), "Geert");

geert.getName = () -> { return "override"; };
System.print(geert.getName());
assertStringEquals(geert.getName(), "override");

System.print("Nested function test");

first = (argument) -> {
	return argument();
};

third = () -> { return "test"; };
thevalue = first(third);
assertStringEquals(thevalue, "test");

if(true) System.print("Works!");
if(false) System.print("Should't work!");

if(true)
{
	System.print("Test completed.");
};

beef = new {
	recipe = "grill";
	
	hello = () -> {
		System.print("Beef says hello!");
	}
};
beef.hello();
System.print("Recipe: "+beef.recipe);
for(b=0 ; b<10 ; b++ ) {
	System.print(""+b);
};
System.print("done");

name = "Geert";
if(name == "Geert") {
 System.print("vos!");
};

//Postfix test
a=6;
b=0;
b= a++;
System.print(""+a);
System.print(""+b);

counter = 100;
while(counter>0) {
	System.print("Counter: "+counter);
	counter--;
	if(counter==50) {
		break;
	} else {
	}
	if(counter<75) {
		continue;
    }
	System.print("Above 75");
}

//TODO: Check why this variable is not set to 0
for(a=0;a<10;a++) {
	System.print("a: "+a);
}

while(a<100) {
	a++;
	System.print("a: "+a);
}
try {
	a = true + 5;
	System.print("Not good");
} catch(a) {
	System.print("Exception caught: "+a.message+" at ("+a.line+")");
};
try {
	throw "Excellent!";
} catch(a) {
	System.print("Exception caught again: "+a.message+" at "+a.location+":"+a.line);
};

modulus = 5 % 2;
System.print("Modulus: "+modulus);
name = "GEert";
System.print(name.lowercase);
System.print("Finished.");

