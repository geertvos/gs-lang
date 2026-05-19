package net.geertvos.gvm.stdlib.io;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class FileModule implements NativeModule {

    @Override
    public String className() {
        return "gs.io.File";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        if (args.isEmpty() || !args.get(0).isString()) {
            throw new NativeError("File: requires a String path argument");
        }
        return NativeValue.instance(new FileInstance(args.get(0).asString()));
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown static method " + method + " on gs.io.File");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Collections.emptyList();
    }

    public static class FileInstance implements NativeInstance {
        private final String path;

        public FileInstance(String path) {
            this.path = path;
        }

        @Override
        public String typeName() {
            return "File";
        }

        @Override
        public List<MethodDescriptor> instanceMethods() {
            return Arrays.asList(
                    new MethodDescriptor("read", 0),
                    new MethodDescriptor("write", 1),
                    new MethodDescriptor("append", 1),
                    new MethodDescriptor("exists", 0),
                    new MethodDescriptor("delete", 0)
            );
        }

        @Override
        public NativeValue callMethod(String method, List<NativeValue> args) throws NativeError {
            try {
                switch (method) {
                    case "read":
                        return NativeValue.string(new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8));
                    case "write": {
                        String data = extractStringArg(args, "write");
                        Files.write(Paths.get(path), data.getBytes(StandardCharsets.UTF_8));
                        return NativeValue.UNDEFINED;
                    }
                    case "append": {
                        String data = extractStringArg(args, "append");
                        try (FileWriter fw = new FileWriter(path, true)) {
                            fw.write(data);
                        }
                        return NativeValue.UNDEFINED;
                    }
                    case "exists":
                        return NativeValue.bool(Files.exists(Paths.get(path)));
                    case "delete":
                        return NativeValue.bool(Files.deleteIfExists(Paths.get(path)));
                    default:
                        throw new NativeError("Unknown method " + method + " on File");
                }
            } catch (IOException e) {
                throw new NativeError(method + " failed: " + e.getMessage());
            }
        }

        private String extractStringArg(List<NativeValue> args, String method) throws NativeError {
            if (args.isEmpty()) {
                throw new NativeError(method + ": expected argument");
            }
            NativeValue arg = args.get(0);
            if (arg.isString()) {
                return arg.asString();
            } else if (arg.isBytes()) {
                return new String(arg.asBytes(), StandardCharsets.UTF_8);
            }
            throw new NativeError(method + ": expected String argument");
        }

        @Override
        public void destroy() {}

        @Override
        public NativeInstance cloneInstance() {
            return new FileInstance(path);
        }
    }
}
