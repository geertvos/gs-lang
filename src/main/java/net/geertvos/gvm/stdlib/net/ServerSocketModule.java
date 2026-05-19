package net.geertvos.gvm.stdlib.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class ServerSocketModule implements NativeModule {

    @Override
    public String className() {
        return "gs.net.ServerSocket";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        int port = args.isEmpty() ? 8080 : args.get(0).asNumber();
        try {
            ServerSocket ss = new ServerSocket(port);
            return NativeValue.instance(new ServerSocketInstance(ss));
        } catch (IOException e) {
            throw new NativeError("Failed to bind to port " + port + ": " + e.getMessage());
        }
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown static method " + method + " on gs.net.ServerSocket");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Collections.emptyList();
    }

    public static class ServerSocketInstance implements NativeInstance {
        private final ServerSocket serverSocket;

        public ServerSocketInstance(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public String typeName() {
            return "ServerSocket";
        }

        @Override
        public List<MethodDescriptor> instanceMethods() {
            return Arrays.asList(
                    new MethodDescriptor("accept", 0),
                    new MethodDescriptor("close", 0)
            );
        }

        @Override
        public NativeValue callMethod(String method, List<NativeValue> args) throws NativeError {
            switch (method) {
                case "accept":
                    try {
                        Socket socket = serverSocket.accept();
                        return NativeValue.instance(new TcpStreamModule.TcpStreamInstance(socket));
                    } catch (IOException e) {
                        throw new NativeError("accept failed: " + e.getMessage());
                    }
                case "close":
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        // ignore
                    }
                    return NativeValue.UNDEFINED;
                default:
                    throw new NativeError("Unknown method " + method + " on ServerSocket");
            }
        }

        @Override
        public void destroy() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // ignore
            }
        }

        @Override
        public NativeInstance cloneInstance() {
            return this;
        }
    }
}
