package xyz.ravensharp.gc.module;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.setting.SettingType;

public class Module {
	protected Minecraft mc = Minecraft.getMinecraft();
	private String modName, description;
	private Category category;

	public Module(String modName, String description, Category category) {
		this.modName = modName;
		this.description = description;
		this.category = category;

		addSetting(new Setting(SettingType.BINDING, 0));
		addSetting(new Setting(SettingType.VISIBILITY, true));
		addSetting(new Setting(SettingType.TOGGLE, false));
	}

	public String getName() {
		return this.modName;
	}

	public String getDescription() {
		return this.description;
	}

	public Category getCategory() {
		return this.category;
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	public void setToggled(boolean toggle) {
		this.getSetting(SettingType.TOGGLE).setBoolean(toggle);
		if (toggle) {
			MinecraftForge.EVENT_BUS.register(this);
			this.onEnable();
		} else {
			MinecraftForge.EVENT_BUS.unregister(this);
			this.onDisable();
		}
	}

	public void toggle() {
		setToggled(!isToggled());
	}

	public boolean isToggled() {
		return this.getSetting(SettingType.TOGGLE).getBoolean();
	}

	public void addSetting(Setting setting) {
		setting.setParentMod(this);
		Sharp.settingManager.addSetting(setting);
	}

	public Setting getSetting(String settingName) {
		return Sharp.settingManager.getSetting(this, settingName);
	}

	public Setting getSetting(SettingType settingType) {
		return Sharp.settingManager.getSetting(this, settingType);
	}

	public void setKeyIfUnbound(int keyCode) {
		if (this.getKeyCode() == 0)
			this.setKeyCode(keyCode);
	}

	public int getKeyCode() {
		return getSetting(SettingType.BINDING).getKeyCode();
	}

	public void setKeyCode(int keyCode) {
		getSetting(SettingType.BINDING).setKeyCode(keyCode);
	}

	public boolean getVisibility() {
		return getSetting(SettingType.VISIBILITY).getBoolean();
	}

	public void toggleVisibility() {
		setVisibility(!getVisibility());
	}

	public void setVisibility(boolean b) {
		getSetting(SettingType.VISIBILITY).setBoolean(b);
	}
}
