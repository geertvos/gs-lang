package net.geertvos.gvm.stdlib.system;

import java.util.Arrays;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class RuntimeModule implements NativeModule {

    @Override
    public String className() {
        return "gs.system.Runtime";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        throw new NativeError("gs.system.Runtime is static-only");
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        if ("print".equals(method)) {
            if (!args.isEmpty()) {
                NativeValue arg = args.get(0);
                if (arg.isString()) {
                    System.out.println(arg.asString());
                } else if (arg.isNumber()) {
                    System.out.println(arg.asNumber());
                } else if (arg.isBoolean()) {
                    System.out.println(arg.asBoolean());
                } else if (arg.isUndefined()) {
                    System.out.println("undefined");
                } else {
                    System.out.println(arg);
                }
            }
            return NativeValue.UNDEFINED;
        }
        throw new NativeError("Unknown static method " + method + " on gs.system.Runtime");
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Arrays.asList(new MethodDescriptor("print", 1));
    }
}
