package xyz.ravensharp.gc.mixins;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;

public class HideSelf {
	@Mixin(FMLHandshakeMessage.ModList.class)
	public abstract static class FMLHandshakeSpoof {
		@Shadow
		private Map<String, String> modTags;

		@Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"))
		public void removeMods(List<ModContainer> modList, CallbackInfo ci) {
			if (Minecraft.getMinecraft().isIntegratedServerRunning())
				return;

			modTags.remove("GhostClient");
		}
	}
}
