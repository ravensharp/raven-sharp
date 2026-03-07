package xyz.ravensharp.gc.module.modules.utility;

import java.security.SecureRandom;
import java.util.ArrayList;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.BlockUtil;
import xyz.ravensharp.gc.utils.MouseUtil;

public class FastPlace extends Module {

	private Thread timerThread;
	private final SecureRandom random = new SecureRandom();
	private long lastClickTime;

	public FastPlace() {
		super("FastPlace", "Advanced Place Clicker.", Category.UTILITY);
		this.addSetting(new Setting("CPS Target", 1D, 50D, 20D));
		this.addSetting(new Setting("Drop Target", 1D, 15D, 3D));
		this.addSetting(new Setting("Increase Target", 1D, 15D, 3D));

		ArrayList<String> randomModes = new ArrayList<>();
		randomModes.add("Very Random");
		randomModes.add("Random");
		randomModes.add("Normal");
		randomModes.add("Stable");
		randomModes.add("Very Stable");
		this.addSetting(new Setting("Random Level", randomModes, randomModes.get(2)));
		this.addSetting(new Setting("Only Click When Placeable", false));
	}

	@Override
	public void onEnable() {
		super.onEnable();
		lastClickTime = System.nanoTime();

		timerThread = new Thread(() -> {
			while (this.isToggled()) {
				if (mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null) {
					MouseUtil.unsilence(1);

					long waitTarget = System.nanoTime() + 10_000_000L;
					while (true) {
						if (System.nanoTime() >= waitTarget) {
							break;
						}
					}

					lastClickTime = System.nanoTime();
					continue;
				}

				ItemStack item = mc.thePlayer.getHeldItem();
				if (item == null || !(item.getItem() instanceof ItemBlock)) {
					MouseUtil.unsilence(1);
					MouseUtil.resetToState(1);

					long waitTarget = System.nanoTime() + 10_000_000L;
					while (true) {
						if (System.nanoTime() >= waitTarget) {
							break;
						}
					}

					lastClickTime = System.nanoTime();
					continue;
				}

				if (this.getSetting("Only Click When Placeable").getBoolean()) {
					if (!BlockUtil.isFocusOnBlock()) {
						MouseUtil.unsilence(1);
						MouseUtil.resetToState(1);

						long waitTarget = System.nanoTime() + 10_000_000L;
						while (true) {
							if (System.nanoTime() >= waitTarget) {
								break;
							}
						}

						lastClickTime = System.nanoTime();
						continue;
					}
				}

				if (!MouseUtil.getActualState(1)) {
					MouseUtil.unsilence(1);
					MouseUtil.resetToState(1);

					long waitTarget = System.nanoTime() + 1_000_000L;
					while (true) {
						if (System.nanoTime() >= waitTarget) {
							break;
						}
					}

					lastClickTime = System.nanoTime();
					continue;
				}

				MouseUtil.silence(1);

				executeClickCycle();
			}
		});
		timerThread.setPriority(Thread.MAX_PRIORITY);
		timerThread.start();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		MouseUtil.unsilence(1);
		MouseUtil.resetToState(1);

		if (timerThread != null) {
			timerThread.interrupt();
		}
	}

	private void executeClickCycle() {
		double targetCPS = getSetting("CPS Target").getDouble();

		String randomMode = getSetting("Random Level").getString();
		double dropTarget = getSetting("Drop Target").getDouble();
		double increaseTarget = getSetting("Increase Target").getDouble();

		double currentCPS = targetCPS + (random.nextGaussian() * getCpsSigma(randomMode));

		double triggerChance = 0.04;
		if (randomMode.equalsIgnoreCase("Stable")) {
			triggerChance = 0.02;
		} else if (randomMode.equalsIgnoreCase("Very Stable")) {
			triggerChance = 0.01;
		}

		double chance = random.nextDouble();
		if (chance < triggerChance) {
			currentCPS -= (random.nextDouble() * dropTarget);
		} else if (chance > (1.0 - triggerChance)) {
			currentCPS += (random.nextDouble() * increaseTarget);
		}

		double logNormalInterval = 1000.0 / currentCPS;

		double holdRatio = 0.2 + (random.nextGaussian() * 0.05);
		if (holdRatio < 0.05)
			holdRatio = 0.05;
		if (holdRatio > 0.5)
			holdRatio = 0.5;

		double holdTimeMs = logNormalInterval * holdRatio;

		long expectedEnd = lastClickTime + (long) (logNormalInterval * 1_000_000L);

		MouseUtil.simulateClick(1);

		long holdTarget = System.nanoTime() + (long) (holdTimeMs * 1_000_000L);
		while (true) {
			if (System.nanoTime() >= holdTarget) {
				break;
			}
		}

		MouseUtil.simulateUnclick(1);

		long currentNanos = System.nanoTime();
		long remainingNanos = expectedEnd - currentNanos;

		if (remainingNanos > 0) {
			long remainTarget = System.nanoTime() + remainingNanos;
			while (true) {
				if (System.nanoTime() >= remainTarget) {
					break;
				}
			}
		}

		lastClickTime = System.nanoTime();
	}

	private double getCpsSigma(String mode) {
		if (mode.equalsIgnoreCase("Very Stable"))
			return 0.5;
		if (mode.equalsIgnoreCase("Stable"))
			return 1.0;
		if (mode.equalsIgnoreCase("Normal"))
			return 1.5;
		if (mode.equalsIgnoreCase("Random"))
			return 3.5;
		if (mode.equalsIgnoreCase("Very Random"))
			return 5;
		return 1.5;
	}
}