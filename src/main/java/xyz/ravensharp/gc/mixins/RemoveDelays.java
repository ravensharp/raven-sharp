package xyz.ravensharp.gc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import xyz.ravensharp.gc.module.modules.utility.DelayRemover;

public class RemoveDelays {

	@Mixin(Minecraft.class)
	public abstract static class HitDelay {
		@Shadow
		private int leftClickCounter;

		@Inject(method = "clickMouse", at = @At("HEAD"))
		private void fixHit(CallbackInfo ci) {
			if (DelayRemover.getBoolean("Hit Delay")) {
				this.leftClickCounter = 0;
			}
		}
	}

	@Mixin(EntityLivingBase.class)
	public abstract static class MouseDelay extends Entity {
		public MouseDelay(World worldIn) {
			super(worldIn);
		}

		@Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
		private void fixMouseDelay(float p, CallbackInfoReturnable<Vec3> cir) {
			if ((Object) this instanceof EntityPlayerSP && DelayRemover.getBoolean("Mouse Delay")) {
				cir.setReturnValue(super.getLook(p));
			}
		}
	}

	@Mixin(PlayerControllerMP.class)
	public abstract static class BreakingDelay {
		@Shadow
		private int blockHitDelay;

		@Inject(method = "updateController", at = @At("HEAD"))
		private void fixBreakingDelay(CallbackInfo ci) {
			if (DelayRemover.getBoolean("Breaking Block Delay")) {
				this.blockHitDelay = 0;
			}
		}
	}

	@Mixin(EntityLivingBase.class)
	public abstract static class JumpDelay {
		@Shadow
		private int jumpTicks;

		@Inject(method = "onLivingUpdate", at = @At("HEAD"))
		private void fixJumpDelay(CallbackInfo ci) {
			if ((Object) this instanceof EntityPlayerSP) {
				if (DelayRemover.getBoolean("Jump Dealy")) {
					this.jumpTicks = 0;
				}
			}
		}
	}
}
