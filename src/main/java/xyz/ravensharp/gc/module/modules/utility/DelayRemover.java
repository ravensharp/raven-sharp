package xyz.ravensharp.gc.module.modules.utility;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class DelayRemover extends Module {
	public DelayRemover() {
		super("DelayRemover", "Remove Delays.", Category.UTILITY);
		this.addSetting(new Setting("Hit Delay", true));
		this.addSetting(new Setting("Mouse Delay", true));
		this.addSetting(new Setting("Breaking Block Delay", true)
				.setDescription("Remove the delay that occurs between breaking blocks."));
		this.addSetting(new Setting("Jump Delay", true));
	}

	public static Module get() {
		return Sharp.moduleManager.getModule("DelayRemover");
	}

	public static boolean getBoolean(String setName) {
		Module mod = get();
		if (mod == null)
			return false;
		if (!mod.isToggled())
			return false;
		Setting set = Sharp.settingManager.getSetting(get(), setName);
		if (set == null)
			return false;
		return set.getBoolean();
	}
}
