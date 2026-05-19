package net.geertvos.gvm.stdlib;

import net.geertvos.gvm.bridge.NativeRegistry;
import net.geertvos.gvm.stdlib.io.BufferedReaderModule;
import net.geertvos.gvm.stdlib.io.FileModule;
import net.geertvos.gvm.stdlib.net.InputStreamModule;
import net.geertvos.gvm.stdlib.net.OutputStreamModule;
import net.geertvos.gvm.stdlib.net.ServerSocketModule;
import net.geertvos.gvm.stdlib.net.SocketModule;
import net.geertvos.gvm.stdlib.net.TcpStreamModule;
import net.geertvos.gvm.stdlib.system.EnvironmentModule;
import net.geertvos.gvm.stdlib.system.RuntimeModule;
import net.geertvos.gvm.stdlib.system.TimeModule;

public class StandardLibrary {

    public static void registerAll(NativeRegistry registry) {
        registry.register(new RuntimeModule());
        registry.register(new ServerSocketModule());
        registry.register(new SocketModule());
        registry.register(new TcpStreamModule());
        registry.register(new OutputStreamModule());
        registry.register(new InputStreamModule());
        registry.register(new BufferedReaderModule());
        registry.register(new FileModule());
        registry.register(new TimeModule());
        registry.register(new EnvironmentModule());
    }
}
