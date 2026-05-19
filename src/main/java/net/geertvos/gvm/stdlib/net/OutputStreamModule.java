package net.geertvos.gvm.stdlib.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class OutputStreamModule implements NativeModule {

    @Override
    public String className() {
        return "gs.net.OutputStream";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        throw new NativeError("gs.net.OutputStream cannot be constructed directly");
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown static method " + method + " on gs.net.OutputStream");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Collections.emptyList();
    }

    public static class OutputStreamInstance implements NativeInstance {
        private final Socket socket;

        public OutputStreamInstance(Socket socket) {
            this.socket = socket;
        }

        @Override
        public String typeName() {
            return "OutputStream";
        }

        @Override
        public List<MethodDescriptor> instanceMethods() {
            return Arrays.asList(
                    new MethodDescriptor("write", 1),
                    new MethodDescriptor("flush", 0),
                    new MethodDescriptor("close", 0)
            );
        }

        @Override
        public NativeValue callMethod(String method, List<NativeValue> args) throws NativeError {
            try {
                OutputStream out = socket.getOutputStream();
                switch (method) {
                    case "write": {
                        if (args.isEmpty()) {
                            throw new NativeError("write: expected argument");
                        }
                        NativeValue arg = args.get(0);
                        byte[] data;
                        if (arg.isBytes()) {
                            data = arg.asBytes();
                        } else if (arg.isString()) {
                            data = arg.asString().getBytes();
                        } else {
                            throw new NativeError("write: expected Bytes or String argument");
                        }
                        out.write(data);
                        return NativeValue.UNDEFINED;
                    }
                    case "flush":
                        out.flush();
                        return NativeValue.UNDEFINED;
                    case "close":
                        socket.shutdownOutput();
                        return NativeValue.UNDEFINED;
                    default:
                        throw new NativeError("Unknown method " + method + " on OutputStream");
                }
            } catch (IOException e) {
                throw new NativeError(method + " failed: " + e.getMessage());
            }
        }

        @Override
        public void destroy() {}

        @Override
        public NativeInstance cloneInstance() {
            return this;
        }
    }
}
