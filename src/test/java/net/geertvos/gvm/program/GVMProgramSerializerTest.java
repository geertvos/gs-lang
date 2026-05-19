package net.geertvos.gvm.program;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.geertvos.gvm.core.BooleanType;
import net.geertvos.gvm.core.FunctionType;
import net.geertvos.gvm.core.GVM;
import net.geertvos.gvm.core.Undefined;
import net.geertvos.gvm.lang.GscriptExceptionHandler;
import net.geertvos.gvm.lang.GscriptValueConverter;
import net.geertvos.gvm.lang.types.NumberType;
import net.geertvos.gvm.lang.types.ObjectType;
import net.geertvos.gvm.lang.types.StringType;
import net.geertvos.gvm.streams.RandomAccessByteStream;

public class GVMProgramSerializerTest {

	@Test
	public void testRoundTrip() throws IOException {
		GVMProgram original = new GVMProgram("test-program", new GscriptExceptionHandler(), new GscriptValueConverter());
		original.registerType(new ObjectType());
		original.registerType(new StringType());
		original.registerType(new NumberType());

		original.addString("hello");
		original.addString("world");

		RandomAccessByteStream code = new RandomAccessByteStream();
		code.add(GVM.NEW);
		code.writeString(new ObjectType().getName());
		code.add(GVM.HALT);

		GVMFunction main = new GVMFunction(code, new ArrayList<>());
		main.setIndex(0);
		main.registerLocalVariable("x");
		main.registerLocalVariable("y");
		main.registerCatchBlock(5, 20, 25);
		original.addFunction(main);

		RandomAccessByteStream code2 = new RandomAccessByteStream();
		code2.add(GVM.LDC_D);
		code2.writeInt(42);
		code2.writeString(new NumberType().getName());
		code2.add(GVM.RETURN);

		GVMFunction func = new GVMFunction(code2, Arrays.asList("a", "b"));
		func.setIndex(1);
		func.registerLocalVariable("tmp");
		original.addFunction(func);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GVMProgramSerializer.writeTo(original, out);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		GVMProgram loaded = GVMProgramSerializer.readFrom(in, new GscriptExceptionHandler(), new GscriptValueConverter(), argCount -> null);

		assertEquals("test-program", loaded.getName());
		assertEquals(2, loaded.getStringConstants().size());
		assertEquals("hello", loaded.getString(0));
		assertEquals("world", loaded.getString(1));

		Map<Integer, GVMFunction> functions = loaded.getFunctions();
		assertEquals(2, functions.size());

		GVMFunction loadedMain = functions.get(0);
		assertEquals(0, loadedMain.getIndex());
		assertEquals(0, loadedMain.getParameters().size());
		assertEquals(2, loadedMain.getLocals().size());
		assertEquals("x", loadedMain.getLocals().get(0));
		assertEquals("y", loadedMain.getLocals().get(1));

		List<int[]> handlers = loadedMain.getExceptionHandlers();
		assertEquals(1, handlers.size());
		assertArrayEquals(new int[]{5, 20, 25}, handlers.get(0));

		assertArrayEquals(code.getBytes(), loadedMain.getBytecode().getBytes());

		GVMFunction loadedFunc = functions.get(1);
		assertEquals(1, loadedFunc.getIndex());
		assertEquals(2, loadedFunc.getParameters().size());
		assertEquals("a", loadedFunc.getParameters().get(0));
		assertEquals("b", loadedFunc.getParameters().get(1));
		assertEquals(1, loadedFunc.getLocals().size());
		assertEquals("tmp", loadedFunc.getLocals().get(0));

		assertArrayEquals(code2.getBytes(), loadedFunc.getBytecode().getBytes());
	}

	@Test(expected = IOException.class)
	public void testInvalidMagic() throws IOException {
		byte[] bad = new byte[]{0, 0, 0, 0};
		GVMProgramSerializer.readFrom(new ByteArrayInputStream(bad), new GscriptExceptionHandler(), new GscriptValueConverter(), argCount -> null);
	}
}
