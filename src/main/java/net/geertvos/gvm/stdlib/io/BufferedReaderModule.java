package net.geertvos.gvm.stdlib.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;
import net.geertvos.gvm.stdlib.net.InputStreamModule;

public class BufferedReaderModule implements NativeModule {

    @Override
    public String className() {
        return "gs.io.BufferedReader";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        if (args.isEmpty() || !args.get(0).isInstance()) {
            throw new NativeError("BufferedReader: requires an InputStream argument");
        }
        NativeInstance inst = args.get(0).asInstance();
        if (!(inst instanceof InputStreamModule.InputStreamInstance)) {
            throw new NativeError("BufferedReader: argument is not an InputStream");
        }
        Socket socket = ((InputStreamModule.InputStreamInstance) inst).getSocket();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return NativeValue.instance(new BufferedReaderInstance(reader));
        } catch (IOException e) {
            throw new NativeError("Failed to create BufferedReader: " + e.getMessage());
        }
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown static method " + method + " on gs.io.BufferedReader");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Collections.emptyList();
    }

    public static class BufferedReaderInstance implements NativeInstance {
        private final BufferedReader reader;

        public BufferedReaderInstance(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public String typeName() {
            return "BufferedReader";
        }

        @Override
        public List<MethodDescriptor> instanceMethods() {
            return Arrays.asList(new MethodDescriptor("readLine", 0));
        }

        @Override
        public NativeValue callMethod(String method, List<NativeValue> args) throws NativeError {
            if ("readLine".equals(method)) {
                try {
                    String line = reader.readLine();
                    return line != null ? NativeValue.string(line) : NativeValue.UNDEFINED;
                } catch (IOException e) {
                    throw new NativeError("readLine failed: " + e.getMessage());
                }
            }
            throw new NativeError("Unknown method " + method + " on BufferedReader");
        }

        @Override
        public void destroy() {
            try {
                reader.close();
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
