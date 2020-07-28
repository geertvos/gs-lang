package net.geertvos.gvm.lang.demo;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Resources;

import net.geertvos.gvm.core.GVM;

public class VirtualMachineDemo {

	public static void main(String[] args) throws IOException {
		URL url = Resources.getResource("VirtualMachine.gs");
		String source = Resources.toString(url, StandardCharsets.UTF_8);
		GVMRuntime runtime = new GVMRuntime();
		GVM gvm = runtime.load(source);
		gvm.run();
		runtime.execute("System.print(\"Message is: \"+VirtualMachine.msg);", gvm);
	}

}
