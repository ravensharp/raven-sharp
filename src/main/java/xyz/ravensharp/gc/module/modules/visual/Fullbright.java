package xyz.ravensharp.gc.module.modules.visual;

import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;

public class Fullbright extends Module {

	public Fullbright() {
		super("Fullbright", "Night Vision", Category.VISUAL);
	}

	private float originalGamma;

	@Override
	public void onEnable() {
		super.onEnable();
		originalGamma = mc.gameSettings.gammaSetting;
		mc.gameSettings.gammaSetting = 100;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		mc.gameSettings.gammaSetting = originalGamma > 10 ? 1 : originalGamma;
	}
}
