package xyz.ravensharp.gc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import xyz.ravensharp.gc.module.modules.visual.CameraEffectRemover;

public class CameraEffects {
	@Mixin(EntityLivingBase.class)
	public abstract static class MixinEntityLivingBase {

		@Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
		private void removeVisualEffects(Potion potionIn, CallbackInfoReturnable<Boolean> cir) {
			if (!((Object) this instanceof EntityPlayerSP))
				return;

			if (CameraEffectRemover.getBoolean("Blindness") && potionIn.id == Potion.blindness.id) {
				cir.setReturnValue(false);
			}
			if (CameraEffectRemover.getBoolean("Nausea") && potionIn.id == Potion.confusion.id) {
				cir.setReturnValue(false);
			}
		}
	}

	@Mixin(ItemRenderer.class)
	public abstract static class MixinItemRenderer {

		@Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
		private void cancelFireOverlay(CallbackInfo ci) {
			if (CameraEffectRemover.getBoolean("FireOverlay")) {
				ci.cancel();
			}
		}
	}

	@Mixin(GuiIngame.class)
	public abstract static class MixinGuiIngame {

		@Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
		private void cancelPumpkin(ScaledResolution scaledRes, CallbackInfo ci) {
			if (CameraEffectRemover.getBoolean("PumpkinBlur")) {
				ci.cancel();
			}
		}

		@Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
		private void cancelPortal(float timeInPortal, ScaledResolution scaledRes, CallbackInfo ci) {
			if (CameraEffectRemover.getBoolean("PortalOverlay")) {
				ci.cancel();
			}
		}
	}

	@Mixin(EntityRenderer.class)
	public abstract static class MixinEntityRenderer {

		@Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
		private void cancelHurtCameraEffect(float partialTicks, CallbackInfo ci) {
			if (CameraEffectRemover.getBoolean("HurtCam")) {
				ci.cancel();
			}
		}

		@Inject(method = "setupFog", at = @At("RETURN"))
		private void modifyLiquidFog(int startCoords, float partialTicks, CallbackInfo ci) {
			Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
			if (entity == null || Minecraft.getMinecraft().theWorld == null)
				return;

			Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(Minecraft.getMinecraft().theWorld, entity,
					partialTicks);

			if (block.getMaterial() == Material.water && CameraEffectRemover.getBoolean("WaterFog")) {
				GlStateManager.setFog(9729);
				GlStateManager.setFogStart(0.0F);
				GlStateManager.setFogEnd(100.0F);
			} else if (block.getMaterial() == Material.lava && CameraEffectRemover.getBoolean("LavaFog")) {
				GlStateManager.setFog(9729);
				GlStateManager.setFogStart(0.0F);
				GlStateManager.setFogEnd(100.0F);
			}
		}

		@Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
		private void controlWeatherRender(float partialTicks, CallbackInfo ci) {
			String weatherMode = CameraEffectRemover.getString("Weather");
			if ("Clear".equalsIgnoreCase(weatherMode)) {
				ci.cancel();
			}
		}
	}

	@Mixin(World.class)
	public abstract static class MixinWorld {

		@Inject(method = "getRainStrength", at = @At("HEAD"), cancellable = true)
		private void overrideRainStrength(float delta, CallbackInfoReturnable<Float> cir) {
			String weatherMode = CameraEffectRemover.getString("Weather");

			if ("Clear".equalsIgnoreCase(weatherMode)) {
				cir.setReturnValue(0.0F);
			} else if ("Raining".equalsIgnoreCase(weatherMode) || "Snowing".equalsIgnoreCase(weatherMode)) {
				cir.setReturnValue(1.0F);
			}
		}
	}

	@Mixin(BiomeGenBase.class)
	public abstract static class MixinBiomeGenBase {

		@Inject(method = "getFloatTemperature", at = @At("HEAD"), cancellable = true)
		private void forceSnowTemperature(BlockPos pos, CallbackInfoReturnable<Float> cir) {
			String weatherMode = CameraEffectRemover.getString("Weather");

			if ("Snowing".equalsIgnoreCase(weatherMode)) {
				cir.setReturnValue(0.0F);
			} else if ("Raining".equalsIgnoreCase(weatherMode)) {
				cir.setReturnValue(1.0F);
			}
		}
	}
}