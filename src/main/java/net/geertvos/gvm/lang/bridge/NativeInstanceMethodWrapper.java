package net.geertvos.gvm.lang.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeMethodWrapper;
import net.geertvos.gvm.bridge.NativeValue;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.program.GVMContext;

public class NativeInstanceMethodWrapper extends NativeMethodWrapper {

    private final NativeInstance instance;
    private final String methodName;
    private final int argCount;

    public NativeInstanceMethodWrapper(NativeInstance instance, String methodName, int argCount) {
        this.instance = instance;
        this.methodName = methodName;
        this.argCount = argCount;
    }

    @Override
    public Value invoke(List<Value> arguments, GVMContext context) {
        Collections.reverse(arguments);
        List<NativeValue> nativeArgs = new ArrayList<>();
        for (Value arg : arguments) {
            nativeArgs.add(NativeValueMarshaller.toNative(arg, context));
        }
        try {
            NativeValue result = instance.callMethod(methodName, nativeArgs);
            return NativeValueMarshaller.toGvm(result, context);
        } catch (NativeError e) {
            throw new RuntimeException("Native method error: " + e.getErrorMessage());
        }
    }

    @Override
    public int argumentCount() {
        return argCount;
    }
}
