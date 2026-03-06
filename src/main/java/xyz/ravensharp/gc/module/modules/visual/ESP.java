package xyz.ravensharp.gc.module.modules.visual;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.ColorUtil;
import xyz.ravensharp.gc.utils.ESPUtil;
import xyz.ravensharp.gc.utils.EntitySettingUtil;

public class ESP extends Module {
	public ESP() {
		super("ESP", "Knowing position of entities through walls.", Category.VISUAL);
		this.addSetting(new Setting("Draw Background", true));
		this.addSetting(new Setting("2D Mode", true));
		this.addSetting(new Setting("Use Name Color", false));

		this.addSetting(new Setting("Background Opacity", 0.0, 100.0, 40.0, false));
		this.addSetting(new Setting("Line Opacity", 0.0, 100.0, 65.0, false));

		ArrayList<String> colors = ColorUtil.getColors();
		this.addSetting(new Setting("Color", colors, colors.get(0)));

		EntitySettingUtil.initSettings(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@SubscribeEvent
	public void onRenderEvent(RenderWorldLastEvent e) {
		if (mc.theWorld == null)
			return;

		float partialTicks = e.partialTicks;

		boolean drawBackground = this.getSetting("Draw Background").getBoolean();
		float bgOpacity = this.getSetting("Background Opacity").getDoubleInFloat() / 100;
		boolean is2D = this.getSetting("2D Mode").getBoolean();
		boolean useNameColor = this.getSetting("Use Name Color").getBoolean();
		float lineOpacity = this.getSetting("Line Opacity").getDoubleInFloat() / 100;

		String cString = this.getSetting("Color").getString();
		Color c = ColorUtil.getColor(cString);
		if (c == null)
			c = Color.WHITE;

		for (Entity entity : mc.theWorld.loadedEntityList) {
			if (EntitySettingUtil.render(this, entity) && entity != mc.thePlayer) {
				Color renderColor = c;

				if (useNameColor) {
					Color nameC = ColorUtil.getMostUsedColor(entity.getDisplayName().toString());
					if (nameC != null)
						renderColor = nameC;
				}

				ESPUtil.renderESP(mc, entity, partialTicks, renderColor, drawBackground, is2D, lineOpacity, bgOpacity);
			}
		}
	}

}