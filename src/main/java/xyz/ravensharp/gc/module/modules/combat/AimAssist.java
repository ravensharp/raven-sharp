package xyz.ravensharp.gc.module.modules.combat;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.EntitySettingUtil;
import xyz.ravensharp.gc.utils.MouseUtil;

public class AimAssist extends Module {

	private EntityLivingBase target;
	private float yawVelocity = 0.0F;
	private float pitchVelocity = 0.0F;
	private Thread aimThread;

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
		this.addSetting(new Setting("FOV", 1.0D, 180.0D, 75.0D));
		this.addSetting(new Setting("Click Only", true));

		this.addSetting(new Setting("Aim Speed", 0.1D, 10.0D, 1.0D));
		this.addSetting(new Setting("Friction", 0.1D, 0.99D, 0.65D));

		EntitySettingUtil.initSettings(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		yawVelocity = 0.0F;
		pitchVelocity = 0.0F;

		aimThread = new Thread(() -> {
			while (this.isToggled()) {
				runAimLogic();
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
		yawVelocity = 0.0F;
		pitchVelocity = 0.0F;
	}

	private void runAimLogic() {
		if (mc.theWorld == null || mc.thePlayer == null || mc.currentScreen != null)
			return;

		if (this.getSetting("Click Only").getBoolean() && !MouseUtil.getActualState(0)) {
			target = null;
			yawVelocity *= 0.5F;
			pitchVelocity *= 0.5F;
			return;
		}

		target = getOptimalTarget();
		if (target != null) {
			aimToTarget(target);
		} else {
			yawVelocity *= 0.5F;
			pitchVelocity *= 0.5F;
		}
	}

	private EntityLivingBase getOptimalTarget() {
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
			targetY = entity.posY + (entity.getEyeHeight() * 0.7);
		}

		double diffX = targetX - playerEyes.xCoord;
		double diffY = targetY - playerEyes.yCoord;
		double diffZ = targetZ - playerEyes.zCoord;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

		float targetYaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float targetPitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

		float yawDiff = MathHelper.wrapAngleTo180_float(targetYaw - mc.thePlayer.rotationYaw);
		float pitchDiff = MathHelper.wrapAngleTo180_float(targetPitch - mc.thePlayer.rotationPitch);

		float friction = (float) this.getSetting("Friction").getDoubleInFloat();
		float aimSpeed = (float) this.getSetting("Aim Speed").getDoubleInFloat();

		yawVelocity += (yawDiff * aimSpeed * 0.001F);
		pitchVelocity += (pitchDiff * aimSpeed * 0.001F);

		yawVelocity *= friction;
		pitchVelocity *= friction;

		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float gcd = f * f * f * 1.2F;

		int deltaMouseX = Math.round(yawVelocity / gcd);
		int deltaMouseY = Math.round(pitchVelocity / gcd);

		float actualYawVelocity = deltaMouseX * gcd;
		float actualPitchVelocity = deltaMouseY * gcd;

		mc.thePlayer.rotationYaw += actualYawVelocity;

		float setPitch = mc.thePlayer.rotationPitch + actualPitchVelocity;
		mc.thePlayer.rotationPitch = MathHelper.clamp_float(setPitch, -90.0F, 90.0F);
	}

	private double getFovDistance(EntityLivingBase entity) {
		double diffX = entity.posX - mc.thePlayer.posX;
		double diffZ = entity.posZ - mc.thePlayer.posZ;
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		return Math.abs(MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw));
	}
}