package xyz.ravensharp.gc.utils;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class KeyboardUtil {

    private static Field keyDownBufferField;
    private static ByteBuffer keyDownBuffer;
    private static Field readBufferField;

    static {
        try {
            keyDownBufferField = Keyboard.class.getDeclaredField("keyDownBuffer");
            keyDownBufferField.setAccessible(true);
            keyDownBuffer = (ByteBuffer) keyDownBufferField.get(null);

            readBufferField = Keyboard.class.getDeclaredField("readBuffer");
            readBufferField.setAccessible(true);
        } catch (Throwable t) {}
    }

    public static boolean getActualState(int key) {
        try {
            if (keyDownBuffer != null && key >= 0 && key < keyDownBuffer.capacity()) {
                return keyDownBuffer.get(key) != 0;
            }
        } catch (Throwable t) {}
        return false;
    }

    public static void simulateKeyPress(int key) {
        enqueue(key, true);
    }

    public static void simulateKeyRelease(int key) {
        enqueue(key, false);
    }
    
    public static void resetToState(int key) {
        Runnable r = () -> {
            boolean physical = getActualState(key);
            enqueue(key, physical);
        };

        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            r.run();
        } else {
            Minecraft.getMinecraft().addScheduledTask(r);
        }
    }

    private static void enqueue(int key, boolean state) {
        Runnable r = () -> {
            try {
                if (readBufferField == null) return;

                ByteBuffer readBuffer = (ByteBuffer) readBufferField.get(null);
                if (readBuffer == null) return;

                int pos = readBuffer.position();
                int limit = readBuffer.limit();

                if (limit + 18 > readBuffer.capacity()) return;

                readBuffer.limit(limit + 18);
                readBuffer.position(limit);

                readBuffer.putInt(key);
                readBuffer.put((byte) (state ? 1 : 0));
                readBuffer.putInt(0);
                readBuffer.putLong(System.nanoTime());
                readBuffer.put((byte) 0);

                readBuffer.position(pos);
            } catch (Throwable t) {}
        };

        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            r.run();
        } else {
            Minecraft.getMinecraft().addScheduledTask(r);
        }
    }
}