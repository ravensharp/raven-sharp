package xyz.ravensharp.gc;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import xyz.ravensharp.gc.config.ConfigManager;
import xyz.ravensharp.gc.gui.clickgui.ClickGui;
import xyz.ravensharp.gc.gui.configgui.ConfigGui;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.module.ModuleManager;
import xyz.ravensharp.gc.setting.SettingManager;

@Mod(modid = Sharp.MODID, version = Sharp.VERSION)
public class Sharp {
	public static final String MODID = "GhostClient";
	public static final String VERSION = "1.0.1";

	public static ClickGui clickGui;
	public static ConfigGui configGui;

	public static ModuleManager moduleManager;
	public static SettingManager settingManager;
	public static ConfigManager configManager;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		settingManager = new SettingManager();
		moduleManager = new ModuleManager();
		configManager = new ConfigManager();
		clickGui = new ClickGui();
		configGui = new ConfigGui();
	}

	@SubscribeEvent
	public void key(KeyInputEvent e) {
		if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null)
			return;
		try {
			if (Keyboard.isCreated()) {
				if (Keyboard.getEventKeyState()) {
					int keyCode = Keyboard.getEventKey();
					if (keyCode <= 0)
						return;
					for (Module m : moduleManager.getModules()) {
						if (keyCode == m.getKeyCode())
							m.toggle();
					}
				}
			}
		} catch (Exception q) {
			q.printStackTrace();
		}
	}
}
