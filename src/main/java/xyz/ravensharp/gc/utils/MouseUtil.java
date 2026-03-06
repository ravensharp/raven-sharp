package xyz.ravensharp.gc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class MouseUtil {

	private static Field buttonsField;
	private static Field readBufferField;
	private static ByteBuffer buttonsBuffer;

	private static boolean lastLeftSentState = false;
	private static boolean lastRightSentState = false;

	private static final boolean[] silenced = new boolean[16];
	private static final boolean[] physicalState = new boolean[16];

	static {
		try {
			buttonsField = Mouse.class.getDeclaredField("buttons");
			buttonsField.setAccessible(true);
			buttonsBuffer = (ByteBuffer) buttonsField.get(null);

			readBufferField = Mouse.class.getDeclaredField("readBuffer");
			readBufferField.setAccessible(true);

			Field implField = Mouse.class.getDeclaredField("implementation");
			implField.setAccessible(true);
			Object originalImpl = implField.get(null);

			Object proxyImpl = Proxy.newProxyInstance(Mouse.class.getClassLoader(),
					new Class<?>[] { Class.forName("org.lwjgl.opengl.InputImplementation") }, new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							if ("readMouse".equals(method.getName()) && args != null && args.length > 0
									&& args[0] instanceof ByteBuffer) {
								ByteBuffer buffer = (ByteBuffer) args[0];

								int beforePos = buffer.position();

								Object result = method.invoke(originalImpl, args);

								int afterPos = buffer.position();

								filterReadBuffer(buffer, beforePos, afterPos);
								return result;
							} else {
								Object result = method.invoke(originalImpl, args);
								if ("pollMouse".equals(method.getName()) && args != null && args.length > 1
										&& args[1] instanceof ByteBuffer) {
									filterButtonsBuffer((ByteBuffer) args[1]);
								}
								return result;
							}
						}
					});
			implField.set(null, proxyImpl);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static void filterReadBuffer(ByteBuffer buffer, int startOffset, int endOffset) {
		if (buffer == null || startOffset >= endOffset)
			return;

		int originalLimit = buffer.limit();

		buffer.position(startOffset);
		buffer.limit(endOffset);

		ByteBuffer temp = ByteBuffer.allocate(endOffset - startOffset);

		while (buffer.remaining() >= 22) {
			buffer.mark();
			byte button = buffer.get();
			byte state = buffer.get();
			buffer.reset();

			if (button >= 0 && button < physicalState.length) {
				physicalState[button] = (state != 0);
			}

			if (button >= 0 && button < silenced.length && silenced[button]) {
				buffer.position(buffer.position() + 22);
			} else {
				byte[] eventBytes = new byte[22];
				buffer.get(eventBytes);
				temp.put(eventBytes);
			}
		}

		temp.flip();
		buffer.position(startOffset);
		buffer.put(temp);

		buffer.limit(originalLimit);
	}

	private static void filterButtonsBuffer(ByteBuffer buttons) {
		if (buttons == null)
			return;

		for (int i = 0; i < physicalState.length; i++) {
			if (i < buttons.capacity()) {
				physicalState[i] = (buttons.get(i) != 0);
			}
		}

		for (int i = 0; i < silenced.length; i++) {
			if (silenced[i] && i < buttons.capacity()) {
				buttons.put(i, (byte) 0);
			}
		}
	}

	public static void silence(int button) {
		if (button >= 0 && button < silenced.length) {
			silenced[button] = true;
		}
	}

	public static void unsilence(int button) {
		if (button >= 0 && button < silenced.length) {
			silenced[button] = false;
		}
	}

	public static boolean isSilenced(int button) {
		return button >= 0 && button < silenced.length && silenced[button];
	}

	public static boolean getActualState(int button) {
		if (button >= 0 && button < physicalState.length) {
			return physicalState[button];
		}
		return false;
	}

	public static boolean getSimulatedState(int button) {
		if (button == 0)
			return lastLeftSentState;
		if (button == 1)
			return lastRightSentState;
		return false;
	}

	public static void simulateClick(int button) {
		enqueue(button, true);
		if (button == 0) {
			lastLeftSentState = true;
		} else if (button == 1) {
			lastRightSentState = true;
		}
	}

	public static void simulateUnclick(int button) {
		enqueue(button, false);
		if (button == 0) {
			lastLeftSentState = false;
		} else if (button == 1) {
			lastRightSentState = false;
		}
	}

	public static void resetToState(int button) {
		boolean actual = getActualState(button);
		if (button == 0) {
			if (lastLeftSentState == actual) {
				return;
			}
			lastLeftSentState = actual;
		} else if (button == 1) {
			if (lastRightSentState == actual) {
				return;
			}
			lastRightSentState = actual;
		}

		Runnable r = () -> {
			enqueue(button, actual);
		};

		if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
			r.run();
		} else {
			Minecraft.getMinecraft().addScheduledTask(r);
		}
	}

	private static void enqueue(int button, boolean state) {
		Runnable r = () -> {
			try {
				if (readBufferField == null)
					return;

				ByteBuffer readBuffer = (ByteBuffer) readBufferField.get(null);
				if (readBuffer == null)
					return;

				int pos = readBuffer.position();
				int limit = readBuffer.limit();

				if (limit + 22 > readBuffer.capacity())
					return;

				readBuffer.limit(limit + 22);
				readBuffer.position(limit);

				readBuffer.put((byte) button);
				readBuffer.put((byte) (state ? 1 : 0));
				readBuffer.putInt(0);
				readBuffer.putInt(0);
				readBuffer.putInt(0);
				readBuffer.putLong(System.nanoTime());

				readBuffer.position(pos);
			} catch (Throwable t) {
			}
		};

		if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
			r.run();
		} else {
			Minecraft.getMinecraft().addScheduledTask(r);
		}
	}
}