package xyz.ravensharp.gc.module.modules.combat;

import java.security.SecureRandom;
import java.util.ArrayList;

import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.BlockUtil;
import xyz.ravensharp.gc.utils.Counter;
import xyz.ravensharp.gc.utils.MouseUtil;

public class Clicker extends Module {

	private Thread timerThread;
	private final SecureRandom random = new SecureRandom();
	private long lastClickTime;
	private Counter counter = new Counter(1, Counter.Unit.SEC);

	public Clicker() {
		super("Clicker", "Advanced Humanized Clicker.", Category.COMBAT);
		this.addSetting(new Setting("Break Block", true));
		this.addSetting(new Setting("Target CPS", 1D, 25D, 15D));

		ArrayList<String> randomModes = new ArrayList<>();
		randomModes.add("Normal");
		randomModes.add("Stable");
		randomModes.add("Very Stable");
		this.addSetting(new Setting("Random Level", randomModes, randomModes.get(0)));

		ArrayList<String> rightClickModes = new ArrayList<>();
		rightClickModes.add("None");
		rightClickModes.add("OnRightHold");
		rightClickModes.add("Always");
		this.addSetting(new Setting("Blockhit Mode", rightClickModes, rightClickModes.get(0)));
		this.addSetting(new Setting("Block Chance", 1D, 100D, 30D));
	}

	@Override
	public void onEnable() {
		super.onEnable();
		lastClickTime = System.nanoTime();

		// silence need fix
		// silence for right click shall only activated only if block hit enabled.

		timerThread = new Thread(() -> {
			while (this.isToggled()) {
				if (mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null) {
					MouseUtil.unsilence(0);
					MouseUtil.unsilence(1);
					preciseSleep(10_000_000L);
					lastClickTime = System.nanoTime();
					continue;
				}

				if (!MouseUtil.getActualState(0)) {
					MouseUtil.unsilence(0);
					MouseUtil.unsilence(1);
					MouseUtil.resetToState(0);
					MouseUtil.resetToState(1);
					// preciseSleep(1_000_000L);
					lastClickTime = System.nanoTime();
					continue;
				}

				if (getSetting("Break Block").getBoolean() && BlockUtil.isFocusOnBlock()) {
					MouseUtil.unsilence(0);
					MouseUtil.resetToState(0);
					// preciseSleep(10_000_000L);
					lastClickTime = System.nanoTime();
					continue;
				}

				MouseUtil.silence(0);
				if (this.isBlockHitEnabled())
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
		MouseUtil.unsilence(0);
		MouseUtil.unsilence(1);
		MouseUtil.resetToState(0);
		MouseUtil.resetToState(1);

		if (timerThread != null) {
			timerThread.interrupt();
		}
	}

	private void executeClickCycle() {
		double targetCPS = getSetting("Target CPS").getDouble();

		String randomMode = getSetting("Random Level").getString();
		String blockHitMode = getSetting("Blockhit Mode").getString();
		double blockChance = getSetting("Block Chance").getDouble();

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

		boolean willBlock = shouldBlock(blockHitMode, blockChance);

		long expectedEnd = lastClickTime + (long) (logNormalInterval * 1_000_000L);

		MouseUtil.simulateClick(0);

		preciseSleep((long) (holdTimeMs * 1_000_000L));

		MouseUtil.simulateUnclick(0);

		if (willBlock) {
			triggerBlockHit(logNormalInterval);
		}

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

	private boolean isBlockHitEnabled() {
		String mode = this.getSetting("Blockhit Mode").getString();
		return !mode.equalsIgnoreCase("None");
	}

	private boolean shouldBlock(String mode, double chance) {
		boolean base = false;
		if (mode.equalsIgnoreCase("Always"))
			base = true;
		else if (mode.equalsIgnoreCase("OnRightHold"))
			base = MouseUtil.getActualState(1);

		return base && (random.nextInt(100) + 1 <= chance);
	}

	private void triggerBlockHit(double currentInterval) {
		long delay = (long) (currentInterval * 0.1 * 1_000_000L);
		long hold = (long) (currentInterval * 0.2 * 1_000_000L);

		new Thread(() -> {
			preciseSleep(delay);
			MouseUtil.simulateClick(1);
			preciseSleep(hold);
			MouseUtil.simulateUnclick(1);
		}).start();
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