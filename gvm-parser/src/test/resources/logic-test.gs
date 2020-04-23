print = (text) -> {
	native("net.geertvos.gvm.parser.GVMIntegrationTest", "print", text);
	return; //TODO: add auto return if missing.
};
assertIntEquals = (value, expected) -> {
	test = value==expected;
	print(" >> Testing value: '"+value+"' against: '" +expected+"', should be '"+test+"'");
	native("net.geertvos.gvm.parser.GVMIntegrationTest", "testEqualsInt", value ,expected);
	return; //TODO: add auto return if missing.
};

assertBoolEquals = (value, expected) -> {
	test = value==expected;
	print(" >> Testing value: '"+value+"' against: '" +expected+"', should be '"+test+"'");
	native("net.geertvos.gvm.parser.GVMIntegrationTest", "testEqualsBoolean", value ,expected);
	return; //TODO: add auto return if missing.
};



print("Starting integration test.");

assertIntEquals(1+1,2);
assertIntEquals(3-1,2);
assertIntEquals(2*2,4);
assertIntEquals(6/2,3);

assertBoolEquals(true,true);
assertBoolEquals(false,false);
assertBoolEquals(true,!false);
assertBoolEquals(!true,false);

assertBoolEquals(true, 1 == 1);
assertBoolEquals(false, 2 == 1);
assertBoolEquals(true, 2 != 1);
assertBoolEquals(false, 2 != 2);

print("Test completed.");


