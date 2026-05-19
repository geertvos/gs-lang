package net.geertvos.gvm.stdlib.system;

import java.util.Arrays;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeModule;
import net.geertvos.gvm.bridge.NativeValue;

public class TimeModule implements NativeModule {

    @Override
    public String className() {
        return "gs.system.Time";
    }

    @Override
    public NativeValue constructor(List<NativeValue> args) throws NativeError {
        throw new NativeError("gs.system.Time is static-only");
    }

    @Override
    public NativeValue callStatic(String method, List<NativeValue> args) throws NativeError {
        switch (method) {
            case "now":
                return NativeValue.number((int) System.currentTimeMillis());
            case "sleep":
                int ms = args.isEmpty() ? 0 : args.get(0).asNumber();
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return NativeValue.UNDEFINED;
            default:
                throw new NativeError("Unknown static method " + method + " on gs.system.Time");
        }
    }

    @Override
    public List<MethodDescriptor> staticMethods() {
        return Arrays.asList(
                new MethodDescriptor("now", 0),
                new MethodDescriptor("sleep", 1)
        );
    }
}
