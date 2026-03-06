package xyz.ravensharp.gc.module.modules.movement;

import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.KeyboardUtil;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", "Automatically presses the sprint keybind.", Category.MOVEMENT);
		this.addSetting(new Setting("Pause Sprint While Invisible", false));
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.PlayerTickEvent event) {
		if (mc.thePlayer == null || mc.theWorld == null)
			return;

		int keyBindSprint = mc.gameSettings.keyBindSprint.getKeyCode();
		if (this.getSetting("Pause Sprint While Invisible").getBoolean()
				&& mc.thePlayer.isPotionActive(Potion.invisibility)) {
			if (mc.thePlayer.isSprinting()) {
				KeyboardUtil.simulateKeyRelease(keyBindSprint);
			}

			return;
		}

		if (!mc.inGameHasFocus || mc.currentScreen != null)
			return;
		if (KeyboardUtil.getActualState(mc.gameSettings.keyBindForward.getKeyCode())) {
			KeyboardUtil.simulateKeyPress(keyBindSprint);
		}
	}

}
