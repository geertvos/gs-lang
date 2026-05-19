package net.geertvos.gvm.stdlib.net;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class SocketModule implements NativeModule {

    @Override
    public String className() {
        return "gs.net.Socket";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        if (args.size() < 2) {
            throw new NativeError("Socket requires host and port arguments");
        }
        String host = args.get(0).asString();
        int port = args.get(1).asNumber();
        try {
            Socket socket = new Socket(host, port);
            return NativeValue.instance(new TcpStreamModule.TcpStreamInstance(socket));
        } catch (IOException e) {
            throw new NativeError("Failed to connect to " + host + ":" + port + ": " + e.getMessage());
        }
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown static method " + method + " on gs.net.Socket");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Collections.emptyList();
    }
}
