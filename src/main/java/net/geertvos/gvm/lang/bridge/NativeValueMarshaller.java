package net.geertvos.gvm.lang.bridge;

import net.geertvos.gvm.bridge.NativeInstance;
import net.geertvos.gvm.bridge.NativeValue;
import net.geertvos.gvm.core.BooleanType;
import net.geertvos.gvm.core.GVMObject;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.core.Value;
import net.geertvos.gvm.lang.types.NumberType;
import net.geertvos.gvm.lang.types.ObjectType;
import net.geertvos.gvm.lang.types.StringType;
import net.geertvos.gvm.program.GVMContext;

public class NativeValueMarshaller {

    public static NativeValue toNative(Value value, GVMContext context) {
        if (value.getType() instanceof StringType) {
            String s = context.getProgram().getString(value.getValue());
            return NativeValue.string(s);
        } else if (value.getType() instanceof NumberType) {
            return NativeValue.number(value.getValue());
        } else if (value.getType() instanceof BooleanType) {
            return NativeValue.bool(value.getValue() > 0);
        } else if (value.getType() instanceof Undefined) {
            return NativeValue.UNDEFINED;
        } else if (value.getType() instanceof ObjectType) {
            Object obj = context.getHeap().getObject(value.getValue());
            if (obj instanceof NativeInstanceObject) {
                NativeInstance inst = ((NativeInstanceObject) obj).getInstance();
                if (inst instanceof ByteArrayInstance) {
                    return NativeValue.bytes(((ByteArrayInstance) inst).getData());
                }
                return NativeValue.instance(inst.cloneInstance());
            }
            return NativeValue.number(value.getValue());
        }
        return NativeValue.number(value.getValue());
    }

    public static Value toGvm(NativeValue nativeVal, GVMContext context) {
        if (nativeVal.isUndefined()) {
            return new Value(0, new Undefined());
        } else if (nativeVal.isString()) {
            int idx = context.getProgram().addString(nativeVal.asString());
            return new Value(idx, new StringType());
        } else if (nativeVal.isNumber()) {
            return new Value(nativeVal.asNumber(), new NumberType());
        } else if (nativeVal.isBoolean()) {
            return new Value(nativeVal.asBoolean() ? 1 : 0, new BooleanType());
        } else if (nativeVal.isInstance()) {
            NativeInstanceObject wrapper = new NativeInstanceObject(nativeVal.asInstance(), context);
            int index = context.getHeap().addObject(wrapper);
            return new Value(index, new ObjectType());
        } else if (nativeVal.isBytes()) {
            ByteArrayInstance byteInst = new ByteArrayInstance(nativeVal.asBytes());
            NativeInstanceObject wrapper = new NativeInstanceObject(byteInst, context);
            int index = context.getHeap().addObject(wrapper);
            return new Value(index, new ObjectType());
        }
        return new Value(0, new Undefined());
    }
}
