package net.geertvos.gvm.parser;

public class TestObject {

	public String getName() {
		return "Geert";
	}
	
	public void setCallback(Callback c) {
		c.onCallback("You called me!");
	}
	
}
