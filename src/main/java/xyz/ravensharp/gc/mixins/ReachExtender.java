package xyz.ravensharp.gc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.EntityRenderer;
import xyz.ravensharp.gc.module.modules.combat.Reach;

public class ReachExtender {
	@Mixin(EntityRenderer.class)
	public abstract static class MixinEntityRenderer {

		@ModifyConstant(method = "getMouseOver", constant = @Constant(doubleValue = 3.0D))
		private double modifyEntityReachConstant(double original3) {
			if (!Reach.amIToggled())
				return original3;
			return Reach.getReach();
		}

		@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getBlockReachDistance()F"))
		private float modifyBoundingBoxReach(PlayerControllerMP instance) {
			float defaultReach = instance.getBlockReachDistance();
			if (!Reach.amIToggled())
				return defaultReach;
			if (!Reach.isSprintingCheckOkay())
				return defaultReach;

			return (float) Reach.getReach();
		}
	}
}
