package net.geertvos.gvm.stdlib.system;

import java.util.Arrays;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class EnvironmentModule implements NativeModule {

    @Override
    public String className() {
        return "gs.system.Environment";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        throw new NativeError("gs.system.Environment is static-only");
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        switch (method) {
            case "get": {
                if (args.isEmpty() || !args.get(0).isString()) {
                    throw new NativeError("Environment.get: expected String key");
                }
                String val = System.getenv(args.get(0).asString());
                return val != null ? NativeValue.string(val) : NativeValue.UNDEFINED;
            }
            case "set":
                throw new NativeError("Environment.set is not supported in Java (System.getenv is read-only)");
            default:
                throw new NativeError("Unknown static method " + method + " on gs.system.Environment");
        }
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Arrays.asList(
                new MethodDescriptor("get", 1),
                new MethodDescriptor("set", 2)
        );
    }
}
