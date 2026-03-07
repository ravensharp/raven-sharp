package xyz.ravensharp.gc.module.modules.movement;

import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.HitBoxUtil;
import xyz.ravensharp.gc.utils.KeyboardUtil;

public class Bridger extends Module {
	private Thread timerThread;

	public Bridger() {
		super("Bridger", "Automatically Sneak and Unsneak for you.", Category.MOVEMENT);
		this.addSetting(new Setting("Trigger Ground Coverage", 0D, 100D, 28D));
		this.addSetting(new Setting("On Lookdown", false));
	}

	public boolean isPitchOkay() {
		if (mc.thePlayer == null)
			return false;
		if (!this.getSetting("On Lookdown").getBoolean()) {
			return true;
		}
		return mc.thePlayer.rotationPitch > 65F;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		timerThread = new Thread(() -> {
			while (this.isToggled()) {
				try {
					Thread.sleep(1);
					mc.addScheduledTask(() -> tasker());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
		timerThread.start();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (timerThread != null) {
			timerThread.interrupt();
		}
		KeyboardUtil.resetToState(mc.gameSettings.keyBindSneak.getKeyCode());
	}

	public void tasker() {
		if (!this.isToggled()) {
			return;
		}
		if (mc.thePlayer == null)
			return;

		int keyBindSneak = mc.gameSettings.keyBindSneak.getKeyCode();
		boolean isPhysicalKeyDown = KeyboardUtil.getActualState(keyBindSneak);

		if (!isPitchOkay()) {
			KeyboardUtil.resetToState(keyBindSneak);
			return;
		}

		double sens = getSetting("Trigger Ground Coverage").getDouble();

		if (!isPhysicalKeyDown && !mc.thePlayer.onGround) {
			KeyboardUtil.resetToState(keyBindSneak);
			return;
		}

		if (HitBoxUtil.calculateSupportPercentage() < sens) {
			if (!mc.thePlayer.isSneaking()) {
				KeyboardUtil.resetToState(keyBindSneak);
				KeyboardUtil.simulateKeyPress(keyBindSneak);
			}
		} else {
			KeyboardUtil.resetToState(keyBindSneak);
		}
	}
}