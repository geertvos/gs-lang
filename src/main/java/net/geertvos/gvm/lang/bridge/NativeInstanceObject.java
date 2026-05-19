package net.geertvos.gvm.lang.bridge;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.geertvos.gvm.bridge.MethodDescriptor;
import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.core.FunctionType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.program.GVMContext;
import net.geertvos.gvm.program.GVMFunction;
import net.geertvos.gvm.streams.RandomAccessByteStream;

public class NativeInstanceObject implements GVMObject {

    private final NativeInstance instance;
    private final Map<String, Value> methods = new HashMap<>();
    private final List<Integer> generatedFunctions = new LinkedList<>();

    public NativeInstanceObject(NativeInstance instance, GVMContext context) {
        this.instance = instance;
        for (MethodDescriptor md : instance.instanceMethods()) {
            NativeInstanceMethodWrapper wrapper = new NativeInstanceMethodWrapper(instance, md.getName(), md.getArgCount());
            int nativeFunction = context.getProgram().add(wrapper);

            RandomAccessByteStream code = new RandomAccessByteStream();
            for (int i = 1; i <= md.getArgCount(); i++) {
                code.add(GVM.LDS);
                code.writeInt(i);
            }
            code.add(GVM.LDC_D);
            code.writeInt(nativeFunction);
            code.writeString(new FunctionType().getName());
            code.add(GVM.NATIVE);
            code.add(GVM.RETURN);

            List<String> paramNames = new LinkedList<>();
            for (int i = 0; i < md.getArgCount(); i++) {
                paramNames.add("arg" + i);
            }
            GVMFunction function = new GVMFunction(code, paramNames);
            int index = context.getProgram().addFunction(function);
            generatedFunctions.add(index);
            generatedFunctions.add(nativeFunction);
            methods.put(md.getName(), new Value(index, new FunctionType(), "Generated for " + md.getName()));
        }
    }

    public NativeInstance getInstance() {
        return instance;
    }

    @Override
    public void setValue(String id, Value v) {
        methods.put(id, v);
    }

    @Override
    public Value getValue(String id) {
        if (methods.containsKey(id)) {
            return methods.get(id);
        }
        return new Value(0, new net.geertvos.gvm.core.Undefined());
    }

    @Override
    public boolean hasValue(String id) {
        return methods.containsKey(id);
    }

    @Override
    public Collection<Value> getValues() {
        return methods.values();
    }

    @Override
    public Collection<String> getKeys() {
        return methods.keySet();
    }

    @Override
    public void preDestroy() {
        instance.destroy();
    }

    @Override
    public GVMObject clone() {
        return this;
    }
}
