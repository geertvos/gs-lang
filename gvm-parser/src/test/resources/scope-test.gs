/*
*/



print = (text) -> {
	native("net.geertvos.gvm.parser.GVMIntegrationTest", "print", text);
	return; //TODO: add auto return if missing.
};
assertStringEquals = (value, expected) -> {
	test = value==expected;
	print(" >> Testing value: '"+value+"' against: '" +expected+"', should be '"+test+"'");
	native("net.geertvos.gvm.parser.GVMIntegrationTest", "testEqualsString", value ,expected);
	return; //TODO: add auto return if missing.
};
print("Starting integration test.");

person = (name) -> {
	this.name = name;
	getName = () -> { return this.name }
	return this
}

print("Object and function scope test");

geert = new person("Geert"); 
print(geert.getName());

assertStringEquals(geert.getName(), "Geert");

geert.getName = () -> { return "override"; };
print(geert.getName());
assertStringEquals(geert.getName(), "override");

print("Nested function test");

first = (argument) -> {
	return argument();
};

third = () -> { return "test"; };
thevalue = first(third);
assertStringEquals(thevalue, "test");

if(true) print("Works!");
if(false) print("Should't work!");

if(true)
{
	print("Test completed.");
};

beef = new {
	recipe = "grill";
};
print("Recipe: "+beef.recipe);
for(b=0 ; b<10 ; b++ ) {
	print(""+b);
};
print("done");

name = "Geert";
if(name == "Geert") {
 print("vos!");
};

//Postfix test
a=6;
b=0;
b= a++;
print(""+a);
print(""+b);

counter = 100;
while(counter>0) {
	print("Counter: "+counter);
	counter--;
	if(counter==50) {
		break;
	} else {
	}
	if(counter<75) {
		continue;
    }
	print("Above 75");
}

//TODO: Check why this variable is not set to 0
for(a=0;a<10;a++) {
	print("a: "+a);
}

while(a<100) {
	a++;
	print("a: "+a);
}
try {
	a = true + 5;
	print("Not good");
} catch(a) {
	print("Exception caught: "+a);
};
try {
	throw "Excellent!";
} catch(a) {
	print("Exception caught: "+a);
};

modulus = 5 % 1;
print("Modulus: "+modulus);
name = "GEert";
print(name.lowercase);
print("Finished.");
