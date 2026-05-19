package net.geertvos.gvm.stdlib.net;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class TcpStreamModule implements NativeModule {

    @Override
    public String className() {
        return "gs.net.TcpStream";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        throw new NativeError("gs.net.TcpStream cannot be constructed directly; use ServerSocket.accept()");
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown static method " + method + " on gs.net.TcpStream");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Collections.emptyList();
    }

    public static class TcpStreamInstance implements NativeInstance {
        private final Socket socket;

        public TcpStreamInstance(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }

        @Override
        public String typeName() {
            return "TcpStream";
        }

        @Override
        public List<MethodDescriptor> instanceMethods() {
            return Arrays.asList(
                    new MethodDescriptor("getInputStream", 0),
                    new MethodDescriptor("getOutputStream", 0),
                    new MethodDescriptor("close", 0)
            );
        }

        @Override
        public NativeValue callMethod(String method, List<NativeValue> args) throws NativeError {
            switch (method) {
                case "getInputStream":
                    return NativeValue.instance(new InputStreamModule.InputStreamInstance(socket));
                case "getOutputStream":
                    return NativeValue.instance(new OutputStreamModule.OutputStreamInstance(socket));
                case "close":
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // ignore
                    }
                    return NativeValue.UNDEFINED;
                default:
                    throw new NativeError("Unknown method " + method + " on TcpStream");
            }
        }

        @Override
        public void destroy() {
            try {
                socket.close();
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
