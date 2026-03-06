package xyz.ravensharp.gc.module.modules.combat;

import net.minecraft.client.Minecraft;
import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class Reach extends Module {
	public Reach() {
		super("Reach", "Extend Your Reach.", Category.COMBAT);
		// MIN, MAX, DEFAULT
		this.addSetting(new Setting("Target Reach", 3D, 6D, 3.2D));
		this.addSetting(new Setting("Sprinting Only", true));
	}

	public static boolean amIToggled() {
		Minecraft mc = Minecraft.getMinecraft();
		Module mod = Sharp.moduleManager.getModule("Reach");
		return mod.isToggled();
	}

	public static double getReach() {
		Minecraft mc = Minecraft.getMinecraft();
		double normal = mc.playerController.extendedReach() ? 5 : 3;
		Module mod = Sharp.moduleManager.getModule("Reach");
		if (!amIToggled())
			return normal;
		return Math.max(Sharp.settingManager.getSetting(mod, "Target Reach").getDouble(), normal);
	}

	public static boolean isSprintingCheckOkay() {
		Minecraft mc = Minecraft.getMinecraft();
		Module mod = Sharp.moduleManager.getModule("Reach");
		boolean isSprintOnly = Sharp.settingManager.getSetting(mod, "Sprinting Only").getBoolean();
		if (isSprintOnly && mc.thePlayer.isSprinting()) {
			return true;
		}
		if (!isSprintOnly)
			return true;

		return false;
	}
}