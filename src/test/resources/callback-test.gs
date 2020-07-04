module CallbackTest;
import System;

testObject = native("net.geertvos.gvm.parser.TestObject", "TestObject");
theCallBack = new {
    this.name = "Geert";

	onCallback = (s) -> {
		System.print(s + name);
		this.name = "Kees";
	}
}
testObject.setCallback(theCallBack);
System.print(theCallBack.name);