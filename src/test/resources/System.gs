Module System;

System = new {
	print = (text) -> {
		native("net.geertvos.gvm.parser.GVMIntegrationTest", "print", text);
	};
}
