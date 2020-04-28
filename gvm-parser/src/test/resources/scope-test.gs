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
	getName = () -> { return this.name; };
	return this;
};

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



