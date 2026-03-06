package xyz.ravensharp.gc.module.modules.visual;

import java.util.ArrayList;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class CameraEffectRemover extends Module {

	public CameraEffectRemover() {
		super("CameraEffectRemover", "Camera Effect Remover.", Category.VISUAL);
		this.addSetting(new Setting("Blindness", true));
		this.addSetting(new Setting("Nausea", true));
		this.addSetting(new Setting("HurtCam", true));
		this.addSetting(new Setting("FireOverlay", true));
		this.addSetting(new Setting("PumpkinBlur", true));
		this.addSetting(new Setting("PortalOverlay", true));
		this.addSetting(new Setting("WaterFog", true));
		this.addSetting(new Setting("LavaFog", true));
		ArrayList<String> weather = new ArrayList<>();
		weather.add("Default");
		weather.add("Clear");
		weather.add("Raining");
		weather.add("Snowing");
		this.addSetting(new Setting("Weather", weather, weather.get(0)));
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	public static Module get() {
		return Sharp.moduleManager.getModule("CameraEffectRemover");
	}

	public static boolean getBoolean(String setName) {
		Module mod = get();
		if (mod == null)
			return false;
		Setting set = Sharp.settingManager.getSetting(mod, setName);
		if (set == null)
			return false;
		return set.getBoolean();
	}

	public static String getString(String setName) {
		Module mod = get();
		if (mod == null)
			return null;
		Setting set = Sharp.settingManager.getSetting(mod, setName);
		if (set == null)
			return null;
		return set.getString();
	}
}