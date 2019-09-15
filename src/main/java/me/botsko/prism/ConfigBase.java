package me.botsko.prism;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigBase {

	protected final Plugin plugin;

	protected FileConfiguration config;

	public ConfigBase(Plugin plugin) {
		this.plugin = plugin;
	}

	public FileConfiguration getConfig() {
		config = plugin.getConfig();
		return config;
	}

	public FileConfiguration getLang(String lang_string) {

		String lang_file = lang_string;
		if (lang_file == null) {
			lang_file = "en-us";
		}

		return loadConfig("languages/", lang_file);

	}

	protected File getDirectory() {
		return new File(plugin.getDataFolder() + "");
	}

	protected File getFilename(String filename) {
		return new File(getDirectory(), filename + ".yml");
	}

	protected FileConfiguration loadConfig(String default_folder, String filename) {
		final File file = getFilename(filename);
		if (file.exists())
			return YamlConfiguration.loadConfiguration(file);

		// Look for defaults in the jar
		try (
				final InputStream defConfigStream = plugin.getResource(default_folder + filename + ".yml");
				final InputStreamReader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)
		) {
			return YamlConfiguration.loadConfiguration(reader);
		} catch (Exception e) {
			return null;
		}
	}

	protected void saveConfig(String filename, FileConfiguration config) {
		final File file = getFilename(filename);
		try {
			config.save(file);
		} catch (final IOException e) {
			// Prism.log("Could not save the configuration file to "+file);
			// Throw exception
		}
	}

	protected void write(String filename, FileConfiguration config) {
		try {
			final BufferedWriter bw = new BufferedWriter(new FileWriter(getFilename(filename), true));
			saveConfig(filename, config);
			bw.flush();
			bw.close();
		} catch (final IOException ignored) {

		}
	}

}
