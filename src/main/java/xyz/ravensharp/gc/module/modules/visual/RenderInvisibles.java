package xyz.ravensharp.gc.module.modules.visual;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.EntitySettingUtil;

public class RenderInvisibles extends Module {
	public RenderInvisibles() {
		super("RenderInvisibles", "Render Invisible Entities.", Category.VISUAL);
		this.addSetting(new Setting("Opacity", 0.0, 100.0, 65.0, false));
		EntitySettingUtil.initSettings(this);
	}

	public static Module get() {
		return Sharp.moduleManager.getModule("RenderInvisibles");
	}

	public static float opacity() {
		return Sharp.settingManager.getSetting(get(), "Opacity").getDoubleInFloat();
	}
}
