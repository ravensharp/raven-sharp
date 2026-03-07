package xyz.ravensharp.gc.module.modules.combat;

import java.security.SecureRandom;
import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.EntitySettingUtil;
import xyz.ravensharp.gc.utils.MouseUtil;

public class AimAssist extends Module {

	private volatile EntityLivingBase target;
	private volatile boolean isCalculatingTarget = false;
	private double yawVelocity = 0.0D;
	private double pitchVelocity = 0.0D;
	private Thread aimThread;
	private final SecureRandom secureRandom = new SecureRandom();

	private long lastTime = 0;
	private double yawRemainder = 0.0D;
	private double pitchRemainder = 0.0D;

	private MouseHelper originalMouseHelper;
	private CustomMouseHelper customMouseHelper;

	public AimAssist() {
		super("AimAssist", "Advanced humanized AimAssist with physics & entity filters.", Category.COMBAT);

		ArrayList<String> targetModes = new ArrayList<>();
		targetModes.add("Closest");
		targetModes.add("Center");
		this.addSetting(new Setting("Target Mode", targetModes, targetModes.get(0)));

		ArrayList<String> priorities = new ArrayList<>();
		priorities.add("FOV");
		priorities.add("Distance");
		priorities.add("HP");
		this.addSetting(new Setting("Priority", priorities, priorities.get(0)));

		this.addSetting(new Setting("Distance", 1.0D, 6.0D, 4.5D));
		this.addSetting(new Setting("FOV", 1.0D, 180.0D, 85.0D));
		this.addSetting(new Setting("Click Only", true));
		this.addSetting(new Setting("Stop On Target", true));
		this.addSetting(new Setting("Human Curve", true));

		this.addSetting(new Setting("Aim Speed", 6.5D, 10.0D, 1.0D));
		this.addSetting(new Setting("Friction", 0.25D, 0.99D, 0.65D));

		EntitySettingUtil.initSettings(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		yawVelocity = 0.0D;
		pitchVelocity = 0.0D;
		target = null;
		isCalculatingTarget = false;

		lastTime = System.nanoTime();
		yawRemainder = 0.0D;
		pitchRemainder = 0.0D;

		if (mc.mouseHelper != null && !(mc.mouseHelper instanceof CustomMouseHelper)) {
			originalMouseHelper = mc.mouseHelper;
			customMouseHelper = new CustomMouseHelper();
			mc.mouseHelper = customMouseHelper;
		}

		aimThread = new Thread(() -> {
			while (this.isToggled()) {
				try {
					runAimLogic();
				} catch (Exception e) {
				}
				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
		aimThread.start();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (aimThread != null) {
			aimThread.interrupt();
			aimThread = null;
		}

		if (originalMouseHelper != null) {
			mc.mouseHelper = originalMouseHelper;
			customMouseHelper = null;
			originalMouseHelper = null;
		}

		yawVelocity = 0.0D;
		pitchVelocity = 0.0D;
		target = null;
	}

	private void runAimLogic() {
		if (mc.theWorld == null || mc.thePlayer == null || mc.currentScreen != null)
			return;

		if (this.getSetting("Click Only").getBoolean() && !MouseUtil.getActualState(0)) {
			target = null;
			yawVelocity *= 0.5D;
			pitchVelocity *= 0.5D;
			return;
		}

		updateTargetSafe();

		if (target != null) {
			aimToTarget(target);
		} else {
			yawVelocity *= 0.5D;
			pitchVelocity *= 0.5D;
		}
	}

	private void updateTargetSafe() {
		if (mc.isCallingFromMinecraftThread()) {
			target = calculateOptimalTarget();
		} else {
			if (!isCalculatingTarget) {
				isCalculatingTarget = true;
				mc.addScheduledTask(() -> {
					try {
						target = calculateOptimalTarget();
					} finally {
						isCalculatingTarget = false;
					}
				});
			}
		}
	}

	private EntityLivingBase calculateOptimalTarget() {
		EntityLivingBase bestTarget = null;
		double maxFov = this.getSetting("FOV").getDouble();
		double maxDist = this.getSetting("Distance").getDouble();
		String priority = this.getSetting("Priority").getString();
		double bestValue = Double.MAX_VALUE;

		for (Entity entity : mc.theWorld.loadedEntityList) {
			if (!(entity instanceof EntityLivingBase))
				continue;
			if (entity == mc.thePlayer)
				continue;

			EntityLivingBase livingEntity = (EntityLivingBase) entity;

			if (!EntitySettingUtil.render(this, livingEntity))
				continue;

			double dist = mc.thePlayer.getDistanceToEntity(livingEntity);
			if (dist > maxDist)
				continue;

			double currentFov = getFovDistance(livingEntity);
			if (currentFov > maxFov)
				continue;

			double compareValue = 0;
			if (priority.equalsIgnoreCase("Distance")) {
				compareValue = dist;
			} else if (priority.equalsIgnoreCase("HP")) {
				compareValue = livingEntity.getHealth();
			} else {
				compareValue = currentFov;
			}

			if (compareValue < bestValue) {
				bestValue = compareValue;
				bestTarget = livingEntity;
			}
		}
		return bestTarget;
	}

	private void aimToTarget(EntityLivingBase entity) {
		boolean isHovering = false;

		if (this.getSetting("Stop On Target").getBoolean()) {
			if (mc.objectMouseOver != null
					&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
				if (mc.objectMouseOver.entityHit == entity) {
					isHovering = true;
				}
			}
		}

		long currentTime = System.nanoTime();
		double deltaTime = (currentTime - lastTime) / 1000000.0D;
		lastTime = currentTime;

		if (deltaTime > 50.0D) {
			deltaTime = 50.0D;
		}

		Vec3 playerEyes = mc.thePlayer.getPositionEyes(1.0F);
		double targetX, targetY, targetZ;
		String mode = this.getSetting("Target Mode").getString();

		if (mode.equalsIgnoreCase("Closest")) {
			AxisAlignedBB bb = entity.getEntityBoundingBox();
			double pad = 0.05D;
			targetX = MathHelper.clamp_double(playerEyes.xCoord, bb.minX + pad, bb.maxX - pad);
			targetY = MathHelper.clamp_double(playerEyes.yCoord, bb.minY + pad, bb.maxY - pad);
			targetZ = MathHelper.clamp_double(playerEyes.zCoord, bb.minZ + pad, bb.maxZ - pad);
		} else {
			targetX = entity.posX;
			targetZ = entity.posZ;
			targetY = entity.posY + (entity.getEyeHeight() * 0.7D);
		}

		double diffX = targetX - playerEyes.xCoord;
		double diffY = targetY - playerEyes.yCoord;
		double diffZ = targetZ - playerEyes.zCoord;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

		double targetYaw = (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0D;
		double targetPitch = -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

		if (this.getSetting("Human Curve").getBoolean()) {
			long time = System.currentTimeMillis();
			double curveYaw = Math.sin(time * 0.003D) * 2.0D;
			double curvePitch = Math.cos(time * 0.004D) * 2.0D;
			targetYaw += curveYaw;
			targetPitch += curvePitch;
		}

		double yawDiff = MathHelper.wrapAngleTo180_float((float) (targetYaw - mc.thePlayer.rotationYaw));
		double pitchDiff = MathHelper.wrapAngleTo180_float((float) (targetPitch - mc.thePlayer.rotationPitch));

		double friction = this.getSetting("Friction").getDouble();
		double baseAimSpeed = this.getSetting("Aim Speed").getDouble();

		if (isHovering) {
			baseAimSpeed *= 0.15D;
			friction *= 0.7D;
		}

		double randomModifier = 0.8D + secureRandom.nextDouble() * 0.4D;

		double pGain = baseAimSpeed * 0.1D * randomModifier;
		double targetYawVelocity = yawDiff * pGain;
		double targetPitchVelocity = pitchDiff * pGain;

		double timeFriction = Math.pow(friction, deltaTime / 50.0D);

		yawVelocity = (yawVelocity * timeFriction) + (targetYawVelocity * (1.0D - timeFriction));
		pitchVelocity = (pitchVelocity * timeFriction) + (targetPitchVelocity * (1.0D - timeFriction));

		double stepYaw = yawVelocity * (deltaTime / 50.0D);
		double stepPitch = pitchVelocity * (deltaTime / 50.0D);

		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float f1 = f * f * f * 8.0F;
		double gcd = (double) f1 * 0.15D;

		int invert = mc.gameSettings.invertMouse ? -1 : 1;

		double pixelX = stepYaw / gcd;
		double pixelY = (-stepPitch / gcd) * invert;

		double totalPixelX = pixelX + yawRemainder;
		double totalPixelY = pixelY + pitchRemainder;

		int injectX = (int) Math.round(totalPixelX);
		int injectY = (int) Math.round(totalPixelY);

		yawRemainder = totalPixelX - injectX;
		pitchRemainder = totalPixelY - injectY;

		if (customMouseHelper != null) {
			customMouseHelper.inject(injectX, injectY);
		}
	}

	private double getFovDistance(EntityLivingBase entity) {
		double diffX = entity.posX - mc.thePlayer.posX;
		double diffZ = entity.posZ - mc.thePlayer.posZ;
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		return Math.abs(MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw));
	}

	private class CustomMouseHelper extends MouseHelper {
		private int injectedDeltaX = 0;
		private int injectedDeltaY = 0;

		public synchronized void inject(int x, int y) {
			this.injectedDeltaX += x;
			this.injectedDeltaY += y;
		}

		@Override
		public void mouseXYChange() {
			super.mouseXYChange();

			synchronized (this) {
				this.deltaX += this.injectedDeltaX;
				this.deltaY += this.injectedDeltaY;
				this.injectedDeltaX = 0;
				this.injectedDeltaY = 0;
			}
		}
	}
}