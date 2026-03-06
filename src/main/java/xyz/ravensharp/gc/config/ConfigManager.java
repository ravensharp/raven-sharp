package xyz.ravensharp.gc.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;

public class ConfigManager {
	private File ROOT_DIR = Minecraft.getMinecraft().mcDataDir;
	private File SETTING_DIR;
	private File CONFIG_DIR;
	private ArrayList<Config> configs;

	public ConfigManager() {
		SETTING_DIR = new File(ROOT_DIR, "GhostClient");
		if (!SETTING_DIR.exists()) {
			SETTING_DIR.mkdirs();
		}
		CONFIG_DIR = new File(SETTING_DIR, "configs");
		if (!CONFIG_DIR.exists()) {
			CONFIG_DIR.mkdirs();
		}
		loadConfigInDir();
	}

	public void loadConfigInDir() {
		File[] files = CONFIG_DIR.listFiles();
		configs = new ArrayList<>();
		if (files == null)
			return;
		for (File file : files) {
			if (!file.isFile())
				continue;
			configs.add(new Config(file));
		}
	}

	public Config getConfig(String name) {
		for (Config config : configs) {
			if (config.getName().equalsIgnoreCase(name)) {
				return config;
			}
		}
		return null;
	}

	public ArrayList<Config> getConfigs() {
		loadConfigInDir();
		return configs;
	}

	public void newConfig(String name) {
		Config config = new Config(name);
		config.saveCurrentState();
		loadConfigInDir();
	}

	public void writeStringToFile(File file, String content) {
		try {
			try (FileWriter writer = new FileWriter(file, false)) {
				writer.write(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getOrCreateConfigFile(String fileName) throws IOException {
		File configurationFile = new File(CONFIG_DIR, fileName);
		if (!configurationFile.exists()) {
			configurationFile.createNewFile();
		}
		return configurationFile;
	}

	public String readFile(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		}
		return sb.toString();
	}

	public String parseForFileName(String s) {
		return s.replace(" ", "_").replaceAll("[^a-zA-Z0-9._]", "");
	}
}
