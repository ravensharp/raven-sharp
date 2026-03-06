package xyz.ravensharp.gc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import xyz.ravensharp.gc.module.modules.visual.RenderInvisibles;
import xyz.ravensharp.gc.utils.EntitySettingUtil;

public class InvisibleRenderer {
	@Mixin(RendererLivingEntity.class)
	public abstract static class MixinRendererLivingEntity<T extends EntityLivingBase> extends Render<T> {

		@Shadow
		protected ModelBase mainModel;

		protected MixinRendererLivingEntity(RenderManager renderManager) {
			super(renderManager);
		}

		@Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
		protected void renderModelPre(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_,
				float p_77036_5_, float p_77036_6_, float scaleFactor, CallbackInfo ci) {

			if (!RenderInvisibles.get().isToggled()) {
				return;
			}

			if (entitylivingbaseIn != null && EntitySettingUtil.render(RenderInvisibles.get(), entitylivingbaseIn)
					&& entitylivingbaseIn.isInvisible() && this.mainModel != null) {
				ci.cancel();

				if (!this.bindEntityTexture(entitylivingbaseIn)) {
					return;
				}

				float opacity = RenderInvisibles.opacity();
				float alpha = Math.max(0.0F, Math.min(1.0F, opacity / 100.0F));

				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

				GlStateManager.depthMask(alpha == 1F);

				GlStateManager.enableBlend();
				GlStateManager.blendFunc(770, 771);
				GlStateManager.alphaFunc(516, 0.003921569F);

				this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_,
						scaleFactor);

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.depthMask(true);

				GlStateManager.popMatrix();
			}
		}
	}
}