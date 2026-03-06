package xyz.ravensharp.gc.config;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import xyz.ravensharp.gc.Sharp;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.setting.SettingType;

public class Config {
	private File file;
	private String name;

	public Config(File f) {
		this.file = f;
		Gson gson = new Gson();
		String jsonString;
		try {
			jsonString = Sharp.configManager.readFile(file);
			JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
			this.name = json.get("name").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Config(String name) {
		this.name = name;
		try {
			this.file = Sharp.configManager.getOrCreateConfigFile(Sharp.configManager.parseForFileName(name) + ".json");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void saveCurrentState() {
		JsonObject root = new JsonObject();
		root.addProperty("name", this.name);
		JsonObject settings = new JsonObject();
		for (Category category : Category.values()) {
			JsonObject settingsOfModsInCategory = new JsonObject();
			for (Module mod : Sharp.moduleManager.getModules(category)) {
				JsonArray settingsOfMod = new JsonArray();
				for (Setting set : Sharp.settingManager.getSettings(mod)) {
					JsonObject setting = new JsonObject();
					setting.addProperty("type", set.getSettingType().toString());
					setting.addProperty("name", set.getSettingName());

					switch (set.getSettingType()) {
					case BINDING:
						setting.addProperty("key_code", set.getKeyCode());
						break;
					case VISIBILITY:
						setting.addProperty("visibility", set.getBoolean());
						break;
					case COMBO:
						setting.addProperty("current_pick", set.getString());
						break;
					case SLIDER:
						if (set.isSliderInteger()) {
							setting.addProperty("current_value_integer", set.getInteger());
						} else {
							setting.addProperty("current_value_double", set.getDouble());
						}
						break;
					case CHECKBOX:
						setting.addProperty("current_boolean", set.getBoolean());
						break;
					case TOGGLE:
						setting.addProperty("current_boolean", set.getBoolean());
					default:
						break;
					}

					settingsOfMod.add(setting);
				}
				settingsOfModsInCategory.add(mod.getName(), settingsOfMod);
			}
			settings.add(category.toString(), settingsOfModsInCategory);
		}
		root.add("settings", settings);

		Gson gson = new Gson();
		Sharp.configManager.writeStringToFile(file, gson.toJson(root));
	}

	public void loadAndApply() {
		try {
			String jsonString = Sharp.configManager.readFile(file);
			if (jsonString == null || jsonString.isEmpty())
				return;

			JsonObject root = new JsonParser().parse(jsonString).getAsJsonObject();
			if (!root.has("settings"))
				return;
			JsonObject categoriesJson = root.getAsJsonObject("settings");

			for (Module mod : Sharp.moduleManager.getModules()) {
				mod.setToggled(false);
			}
			for (Setting set : Sharp.settingManager.getSettings()) {
				set.reset();
			}

			for (Category category : Category.values()) {
				String categoryKey = category.toString();
				if (!categoriesJson.has(categoryKey))
					continue;

				JsonObject modsInCategoryJson = categoriesJson.getAsJsonObject(categoryKey);

				for (Module mod : Sharp.moduleManager.getModules(category)) {
					String modKey = mod.getName();
					if (!modsInCategoryJson.has(modKey))
						continue;

					JsonArray settingsArray = modsInCategoryJson.getAsJsonArray(modKey);

					for (int i = 0; i < settingsArray.size(); i++) {
						JsonObject settingData = settingsArray.get(i).getAsJsonObject();
						String settingName = settingData.get("name").getAsString();

						Setting set = Sharp.settingManager.getSetting(mod, settingName);
						if (set == null)
							continue;

						switch (set.getSettingType()) {
						case BINDING:
							if (settingData.has("key_code"))
								set.setKeyCode(settingData.get("key_code").getAsInt());
							break;
						case VISIBILITY:
							if (settingData.has("visibility"))
								set.setBoolean(settingData.get("visibility").getAsBoolean());
							break;
						case COMBO:
							if (settingData.has("current_pick"))
								set.setString(settingData.get("current_pick").getAsString());
							break;
						case SLIDER:
							if (set.isSliderInteger()) {
								if (settingData.has("current_value_integer"))
									set.setInteger(settingData.get("current_value_integer").getAsInt());
							} else {
								if (settingData.has("current_value_double"))
									set.setDouble(settingData.get("current_value_double").getAsDouble());
							}
							break;
						case CHECKBOX:
							if (settingData.has("current_boolean"))
								set.setBoolean(settingData.get("current_boolean").getAsBoolean());
							break;
						case TOGGLE:
							if (settingData.has("current_boolean"))
								set.setBoolean(settingData.get("current_boolean").getAsBoolean());
							break;
						default:
							break;
						}
					}
				}
			}

			for (Module mod : Sharp.moduleManager.getModules()) {
				Setting s = mod.getSetting(SettingType.TOGGLE);
				if (s != null) {
					mod.setToggled(s.getBoolean());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		this.file.delete();
	}
}