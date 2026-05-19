package net.geertvos.gvm.stdlib.net;

import java.net.Socket;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class InputStreamModule implements NativeModule {

    @Override
    public String className() {
        return "gs.net.InputStream";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        throw new NativeError("gs.net.InputStream cannot be constructed directly");
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown static method " + method + " on gs.net.InputStream");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Collections.emptyList();
    }

    public static class InputStreamInstance implements NativeInstance {
        private final Socket socket;

        public InputStreamInstance(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }

        @Override
        public String typeName() {
            return "InputStream";
        }

        @Override
        public List<MethodDescriptor> instanceMethods() {
            return Collections.emptyList();
        }

        @Override
        public NativeValue callMethod(String method, List<NativeValue> args) throws NativeError {
            throw new NativeError("Unknown method " + method + " on InputStream");
        }

        @Override
        public void destroy() {}

        @Override
        public NativeInstance cloneInstance() {
            return this;
        }
    }
}
