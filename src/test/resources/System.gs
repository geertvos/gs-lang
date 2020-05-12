module System;

print = (text) -> {
	native("net.geertvos.gvm.parser.GVMIntegrationTest", "print", text);
};
