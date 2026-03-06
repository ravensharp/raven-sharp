package xyz.ravensharp.gc.module.modules.visual;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.ColorUtil;

public class Hud extends Module {
	public Hud() {
		super("Hud", "Display toggled modules on screen.", Category.VISUAL);
		ArrayList<String> positions = new ArrayList<>();
		positions.add("Top Left");
		positions.add("Bottom Left");
		positions.add("Top Right");
		positions.add("Bottom Right");
		this.addSetting(new Setting("Position", positions, positions.get(0)));
		ArrayList<String> colors = ColorUtil.getColors();
		colors.add("Rainbow");
		this.addSetting(new Setting("Color", colors, colors.get(0)));
		this.addSetting(new Setting("Render Text Shadow", true));
		this.addSetting(new Setting("Background Opacity", 0.0, 100.0, 65.0, false));
		this.addSetting(new Setting("Brand Logo", true));
	}

	@SubscribeEvent
	public void onRenderEvent(RenderGameOverlayEvent.Post e) {
		if (e.type != RenderGameOverlayEvent.ElementType.TEXT)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null || mc.theWorld == null)
			return;

		FontRenderer fr = mc.fontRendererObj;
		ScaledResolution sr = new ScaledResolution(mc);

		ArrayList<Module> enabledMods = new ArrayList<>();
		for (Module mod : Sharp.moduleManager.getModules()) {
			if (mod.isToggled() && mod.getVisibility()) {
				enabledMods.add(mod);
			}
		}

		enabledMods.sort((m1, m2) -> Integer.compare(fr.getStringWidth(m2.getName()), fr.getStringWidth(m1.getName())));

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

		String positionStr = this.getSetting("Position").getString();
		String colorStr = this.getSetting("Color").getString();
		boolean renderShadow = this.getSetting("Render Text Shadow").getBoolean();
		float bgOpacity = this.getSetting("Background Opacity").getDoubleInFloat() / 100.0f;
		boolean showBrand = this.getSetting("Brand Logo").getBoolean();

		boolean isRight = positionStr.contains("Right");
		boolean isBottom = positionStr.contains("Bottom");

		int bgAlpha = (int) (bgOpacity * 255.0f);
		int bgColor = (bgAlpha << 24);

		float logoScale = 2.0f;
		String logoText = "Raven#";
		int logoWidth = (int) (fr.getStringWidth(logoText) * logoScale);
		int logoHeight = (int) (fr.FONT_HEIGHT * logoScale);

		int y = isBottom ? sr.getScaledHeight() - fr.FONT_HEIGHT - 4 : 4;
		int yDirection = isBottom ? -(fr.FONT_HEIGHT + 2) : (fr.FONT_HEIGHT + 2);

		if (showBrand) {
			if (isBottom) {
				y -= logoHeight + 4;
			} else {
				y += logoHeight + 4;
			}

			GlStateManager.pushMatrix();

			float logoX = isRight ? sr.getScaledWidth() - logoWidth - 4 : 4;
			float logoY = isBottom ? sr.getScaledHeight() - logoHeight - 4 : 4;

			GlStateManager.translate(logoX, logoY, 0);
			GlStateManager.scale(logoScale, logoScale, logoScale);

			int logoColor = -1;
			if (colorStr.equalsIgnoreCase("Rainbow")) {
				logoColor = Color.HSBtoRGB((System.currentTimeMillis()) % 2000L / 2000.0f, 0.8f, 0.8f);
			} else {
				Color awtColor = ColorUtil.getColor(colorStr);
				if (awtColor != null) {
					logoColor = awtColor.getRGB();
				}
			}

			if (renderShadow) {
				fr.drawStringWithShadow(logoText, 0, 0, logoColor);
			} else {
				fr.drawString(logoText, 0, 0, logoColor);
			}

			GlStateManager.popMatrix();
		}

		int count = 0;
		for (Module mod : enabledMods) {
			String str = mod.getName();
			GlStateManager.enableTexture2D();

			int width = fr.getStringWidth(str);
			int x = isRight ? sr.getScaledWidth() - width - 4 : 4;

			int color = -1;
			if (colorStr.equalsIgnoreCase("Rainbow")) {
				color = Color.HSBtoRGB((System.currentTimeMillis() - (count * 100)) % 2000L / 2000.0f, 0.8f, 0.8f);
			} else {
				Color awtColor = ColorUtil.getColor(colorStr);
				if (awtColor != null) {
					color = awtColor.getRGB();
				}
			}

			if (bgAlpha > 0) {
				Gui.drawRect(x - 2, y - 1, x + width + 2, y + fr.FONT_HEIGHT + 1, bgColor);
			}

			if (renderShadow) {
				fr.drawStringWithShadow(str, x, y, color);
			} else {
				fr.drawString(str, x, y, color);
			}

			y += yDirection;
			count++;
		}

		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}