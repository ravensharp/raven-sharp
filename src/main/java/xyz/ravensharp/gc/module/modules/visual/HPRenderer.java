package xyz.ravensharp.gc.module.modules.visual;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.ESPUtil;
import xyz.ravensharp.gc.utils.EntitySettingUtil;

public class HPRenderer extends Module {
	public HPRenderer() {
		super("HPRenderer", "Render HP Bar side of player.", Category.VISUAL);
		ArrayList<String> pos = new ArrayList<>();
		pos.add("Left");
		pos.add("Right");
		this.addSetting(new Setting("Position", pos, pos.get(0)));
		this.addSetting(new Setting("Render Through Wall", false));

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

		Boolean isLeft = this.getSetting("Position").getString().equalsIgnoreCase("Left");
		Boolean renderThroughWall = this.getSetting("Render Through Wall").getBoolean();

		for (Entity entity : mc.theWorld.loadedEntityList) {
			if (EntitySettingUtil.render(this, entity) && entity != mc.thePlayer) {
				ESPUtil.renderHPBar(mc, entity, partialTicks, isLeft, renderThroughWall);
			}
		}
	}

}