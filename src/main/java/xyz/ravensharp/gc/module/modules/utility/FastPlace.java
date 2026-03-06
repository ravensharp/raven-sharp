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
		this.addSetting(new Setting("Target CPS", 1D, 50D, 20D));

		ArrayList<String> randomModes = new ArrayList<>();
		randomModes.add("Normal");
		randomModes.add("Stable");
		randomModes.add("Very Stable");
		this.addSetting(new Setting("Random Level", randomModes, randomModes.get(0)));
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
					preciseSleep(10_000_000L);
					lastClickTime = System.nanoTime();
					continue;
				}

				ItemStack item = mc.thePlayer.getHeldItem();
				if (item == null)
					continue;
				if (!(item.getItem() instanceof ItemBlock))
					continue;
				if (this.getSetting("Only Click When Placeable").getBoolean()) {
					if (!BlockUtil.isFocusOnBlock()) {
						continue;
					}
				}

				if (!MouseUtil.getActualState(1)) {
					MouseUtil.unsilence(1);
					MouseUtil.resetToState(1);
					preciseSleep(1_000_000L);
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
		double targetCPS = getSetting("Target CPS").getDouble();
		String randomMode = getSetting("Random Level").getString();

		double currentCPS = targetCPS + (random.nextGaussian() * getCpsSigma(randomMode));

		double chance = random.nextDouble();
		if (chance < 0.04) {
			currentCPS -= (random.nextDouble() * 2.0 + 3.0);
		} else if (chance > 0.97) {
			currentCPS += (random.nextDouble() * 2.0 + 3.0);
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

		preciseSleep((long) (holdTimeMs * 1_000_000L));

		MouseUtil.simulateUnclick(1);

		long currentNanos = System.nanoTime();
		long remainingNanos = expectedEnd - currentNanos;

		if (remainingNanos > 0) {
			preciseSleep(remainingNanos);
		}

		lastClickTime = System.nanoTime();
	}

	private double getCpsSigma(String mode) {
		if (mode.equalsIgnoreCase("Very Stable"))
			return 0.5;
		if (mode.equalsIgnoreCase("Stable"))
			return 1.0;
		return 1.5;
	}

	private void preciseSleep(long nanos) {
		if (nanos <= 0)
			return;
		long end = System.nanoTime() + nanos;
		while (System.nanoTime() < end) {
			long remaining = end - System.nanoTime();
			if (remaining > 10_000_000L) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}
}