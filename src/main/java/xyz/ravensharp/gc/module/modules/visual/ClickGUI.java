package xyz.ravensharp.gc.module.modules.visual;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class ClickGUI extends Module {

	protected static ArrayList<Integer> keys = new ArrayList<>();

	public ClickGUI() {
		super("ClickGUI", "Toggle and configure modules.", Category.VISUAL);
		this.setKeyIfUnbound(Keyboard.KEY_RSHIFT);

		this.addSetting(new Setting("Move While GUI Open", true));

		keys.add(mc.gameSettings.keyBindForward.getKeyCode());
		keys.add(mc.gameSettings.keyBindBack.getKeyCode());
		keys.add(mc.gameSettings.keyBindRight.getKeyCode());
		keys.add(mc.gameSettings.keyBindLeft.getKeyCode());
		keys.add(mc.gameSettings.keyBindJump.getKeyCode());
		keys.add(mc.gameSettings.keyBindSprint.getKeyCode());
		keys.add(mc.gameSettings.keyBindSneak.getKeyCode());
	}

	@Override
	public void onEnable() {
		super.onEnable();
		mc.displayGuiScreen(Sharp.clickGui);
		this.setToggled(false);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	public static ArrayList<Integer> getKeys() {
		return keys;
	}

	public static boolean isMoveWhileOpen() {
		return Sharp.settingManager.getSetting(Sharp.moduleManager.getModule("ClickGUI"), "Move While GUI Open")
				.getBoolean();
	}
}
