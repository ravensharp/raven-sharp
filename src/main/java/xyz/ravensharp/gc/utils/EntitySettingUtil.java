package xyz.ravensharp.gc.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;

public class EntitySettingUtil {
	public static void initSettings(Module mod) {
		if (mod == null)
			return;
		mod.addSetting(new Setting("Player", true));
		mod.addSetting(new Setting("Mobs", true));
		mod.addSetting(new Setting("Animals", true));
		mod.addSetting(new Setting("Villagers", true));
		mod.addSetting(new Setting("Golems", true));
		mod.addSetting(new Setting("Items", true));
		mod.addSetting(new Setting("Others", true));
	}

	public static boolean render(Module mod, Entity entity) {
		if (mod == null || entity == null || entity instanceof EntityXPOrb)
			return false;

		if (entity instanceof EntityPlayer)
			return mod.getSetting("Player").getBoolean();
		else if (entity instanceof IMob)
			return mod.getSetting("Mobs").getBoolean();
		else if (entity instanceof EntityAnimal || entity instanceof EntityWaterMob
				|| entity instanceof EntityAmbientCreature)
			return mod.getSetting("Animals").getBoolean();
		else if (entity instanceof EntityVillager)
			return mod.getSetting("Villagers").getBoolean();
		else if (entity instanceof EntityGolem)
			return mod.getSetting("Golems").getBoolean();
		else if (entity instanceof EntityItem)
			return mod.getSetting("Items").getBoolean();
		else if (entity instanceof EntityLiving)
			return mod.getSetting("Others").getBoolean();

		return false;
	}
}