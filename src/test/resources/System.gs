module System;
import Native;

print = (text) -> {
	native("net.geertvos.gvm.runtime.Runtime", "print", text);
};
