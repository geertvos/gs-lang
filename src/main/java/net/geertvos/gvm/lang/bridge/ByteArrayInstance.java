package net.geertvos.gvm.lang.bridge;

import java.util.Collections;
import java.util.List;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeError;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeValue;

public class ByteArrayInstance implements NativeInstance {

    private final byte[] data;

    public ByteArrayInstance(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String typeName() {
        return "ByteArray";
    }

    @Override
    public List<MethodDescriptor> instanceMethods() {
        return Collections.emptyList();
    }

    @Override
    public NativeValue callMethod(String method, List<NativeValue> args) throws NativeError {
        throw new NativeError("Unknown method " + method + " on ByteArray");
    }

    @Override
    public void destroy() {}

    @Override
    public NativeInstance cloneInstance() {
        return new ByteArrayInstance(data.clone());
    }
}
