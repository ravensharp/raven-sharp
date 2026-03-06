package xyz.ravensharp.gc.setting;

import java.util.ArrayList;

import xyz.ravensharp.gc.module.Module;

public class SettingManager {
	private ArrayList<Setting> settings;

	public SettingManager() {
		settings = new ArrayList<>();
	}

	public void addSetting(Setting set) {
		settings.add(set);
	}

	public Setting getSetting(Module mod, String settingName) {
		for (Setting set : settings) {
			if (!set.getParentMod().equals(mod))
				continue;
			if (set.getSettingName().equalsIgnoreCase(settingName)) {
				return set;
			}
		}
		return null;
	}

	public Setting getSetting(Module mod, SettingType type) {
		for (Setting set : settings) {
			if (!set.getParentMod().equals(mod))
				continue;
			if (set.getSettingType().equals(type)) {
				return set;
			}
		}
		return null;
	}

	public ArrayList<Setting> getSettings(Module mod) {
		ArrayList<Setting> temp = new ArrayList<>();
		for (Setting set : settings) {
			if (set.getParentMod().equals(mod))
				temp.add(set);
		}
		return temp;
	}

	public ArrayList<Setting> getSettings() {
		return this.settings;
	}
}
