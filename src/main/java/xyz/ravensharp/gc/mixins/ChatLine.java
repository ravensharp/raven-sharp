package xyz.ravensharp.gc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.GuiNewChat;

public class ChatLine {
	@Mixin(GuiNewChat.class)
	public static class ChatLimit {
		@ModifyConstant(method = "setChatLine", constant = @Constant(intValue = 100))
		private int fixChat(int original) {
			return 500000000;
		}
	}
}
