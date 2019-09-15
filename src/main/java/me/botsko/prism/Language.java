package me.botsko.prism;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Hashtable;
import java.util.Map.Entry;

public class Language {
	protected final FileConfiguration lang;

	public Language(FileConfiguration lang) {
		this.lang = lang;
	}

	public String getString(String key) {
		if (lang != null) {
			final String msg = lang.getString(key);
			if (msg != null) {
				return colorize(msg);
			}
		}
		return "";
	}

	public String getString(String key, Hashtable<String, String> replacer) {
		String msg = getString(key);
		if (!replacer.isEmpty()) {
			for (final Entry<String, String> entry : replacer.entrySet()) {
				msg = msg.replace("%(" + entry.getKey() + ")", entry.getValue());
			}
		}
		return msg;
	}

	protected String colorize(String text) {
		return text.replaceAll("&", "§");
	}

}