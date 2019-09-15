package me.botsko.prism.utils;

import com.google.common.base.CaseFormat;
import com.google.gson.JsonParser;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionMessage;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.appliers.PrismProcessType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MiscUtils {

	public static int clampRadius(Player player, int desiredRadius, PrismProcessType processType,
								  FileConfiguration config) {

		if (desiredRadius <= 0) {
			return config.getInt("prism.near.default-radius");
		}

		// Safety checks for max lookup radius
		int max_lookup_radius = config.getInt("prism.queries.max-lookup-radius");
		if (max_lookup_radius <= 0) {
			max_lookup_radius = 5;
			Prism.log("Max lookup radius may not be lower than one. Using safe inputue of five.");
		}

		// Safety checks for max applier radius
		int max_applier_radius = config.getInt("prism.queries.max-applier-radius");
		if (max_applier_radius <= 0) {
			max_applier_radius = 5;
			Prism.log("Max applier radius may not be lower than one. Using safe inputue of five.");
		}

		// Does the radius exceed the configured max?
		if (processType.equals(PrismProcessType.LOOKUP) && desiredRadius > max_lookup_radius) {
			// If player does not have permission to override the max
			if (player != null && !player.hasPermission("prism.override-max-lookup-radius")) {
				return max_lookup_radius;
			}
			// Otherwise non-player
			return desiredRadius;
		}
		else if (!processType.equals(PrismProcessType.LOOKUP) && desiredRadius > max_applier_radius) {
			// If player does not have permission to override the max
			if (player != null && !player.hasPermission("prism.override-max-applier-radius")) {
				return max_applier_radius;
			}
			// Otherwise non-player
			return desiredRadius;
		}
		else {
			// Otherwise, the radius is valid and is not exceeding max
			return desiredRadius;
		}
	}

	public static String paste(Prism prism, String results) {
		if (!prism.getConfig().getBoolean("prism.paste.enable")) {
			return Prism.messenger
					.playerError("Hastebin support is currently disabled by config.");
		}

		String hastebin = "https://hastebin.com/";
		String result;

		HttpURLConnection connection = null;

		try {
			URL url = new URL(hastebin + "documents");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(results);
			wr.flush();
			wr.close();
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			result = hastebin + new JsonParser().parse(rd.readLine()).getAsJsonObject().get("key").getAsString();
		} catch (final Exception ex) {
			Prism.debug(ex.toString());
			result = Prism.messenger.playerError("Unable to paste results (" + ChatColor.YELLOW + ex.getMessage() + ChatColor.RED + ").");
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return result;
	}

	public static List<String> getStartingWith(String start, Iterable<String> options, boolean caseSensitive) {
		final List<String> result = new ArrayList<>();
		if (caseSensitive) {
			for (final String option : options) {
				if (option.startsWith(start))
					result.add(option);
			}
		} else {
			start = start.toLowerCase();
			for (final String option : options) {
				if (option.toLowerCase().startsWith(start))
					result.add(option);
			}
		}

		return result;
	}

	public static List<String> getStartingWith(String arg, Iterable<String> options) {
		return getStartingWith(arg, options, true);
	}

	public static void dispatchAlert(String msg, List<String> commands) {
		String colorized = TypeUtils.colorize(msg);
		String stripped = ChatColor.stripColor(colorized);
		for (String command : commands) {
			if (command.equals("examplecommand <alert>"))
				continue;
			String processedCommand = command.replace("<alert>", stripped);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
		}
	}

	public static String getEntityName(Entity entity) {
		if (entity == null)
			return "unknown";
		if (entity.getType() == EntityType.PLAYER)
			return entity.getName();
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, entity.getType().name());
	}

	public static BaseComponent getPreviousButton() {
		TextComponent textComponent = new TextComponent(" [<< Prev]");
		textComponent.setColor(ChatColor.GRAY);
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
				new TextComponent("Click to view the previous page")}));
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pr pg p"));
		return textComponent;
	}

	public static BaseComponent getNextButton() {
		TextComponent textComponent = new TextComponent("           ");
		textComponent.setColor(ChatColor.GRAY);
		textComponent.addExtra(getNextButtonComponent());
		return textComponent;
	}

	private static BaseComponent getNextButtonComponent() {
		TextComponent textComponent = new TextComponent("[Next >>]");
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
				new TextComponent("Click to view the next page")}));
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pr pg n"));
		return textComponent;
	}

	public static BaseComponent getPrevNextButtons() {
		TextComponent textComponent = new TextComponent();
		textComponent.setColor(ChatColor.GRAY);
		textComponent.addExtra(getPreviousButton());
		textComponent.addExtra(" | ");
		textComponent.addExtra(getNextButtonComponent());
		return textComponent;
	}

	/**
	 * Serializes given Bukkit object to a base64 string, for data storage. Uses
	 * {@link BukkitObjectOutputStream}, so data of items should serialize properly. If given null,
	 * will just return null silently.
	 *
	 * @param obj ConfigurationSerializable to serialize to a base64 string
	 * @return Base64 representation of the given object, or null if it failed to serialize
	 */
	public static String serializeToBase64(ConfigurationSerializable obj) {
		if (obj == null)
			return null;

		// Won't use try-with-resources here; ByteArrayOutputStream requires another try/catch
		// This generates garbage, but unfortunately BukkitObjectOutputStream.reset() is broken
		ByteArrayOutputStream outputStream;
		BukkitObjectOutputStream dataObject;

		try {
			outputStream = new ByteArrayOutputStream();
			dataObject = new BukkitObjectOutputStream(outputStream);
			dataObject.writeObject(obj);
			dataObject.close();
			outputStream.close();

			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deserializes given base64 string to Bukkit object, for data restore. Uses
	 * {@link BukkitObjectInputStream}, so data of items should deserialize properly. If given null,
	 * will just return null silently.
	 *
	 * @param base64 Base64 string to deserialize into a ConfigurationSerializable
	 * @return A ConfigurationSerializable object, or null if it failed to deserialize
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ConfigurationSerializable> T deserializeFromBase64(String base64) {
		if (base64 == null)
			return null;

		// Won't use try-with-resources here; too noisy
		ByteArrayInputStream inputStream;
		BukkitObjectInputStream dataInput;

		try {
			byte[] decoded = Base64Coder.decodeLines(base64);

			inputStream = new ByteArrayInputStream(decoded);
			dataInput = new BukkitObjectInputStream(inputStream);

			return (T) dataInput.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T getEnum(String from, T fallback) {
		if (from != null)
			try {
				return (T) Enum.valueOf(fallback.getClass(), from.toUpperCase());
			} catch (IllegalArgumentException ignored) {
			}
		return fallback;
	}

	public static String niceName(String in) {
		String[] parts = in.replace("_", " ").trim().split("", 2);
		return parts[0].toUpperCase() + parts[1].toLowerCase();
	}

	public static String niceLower(String in) {
		return in.replace("_", " ").trim().toLowerCase();
	}

	public static void sendClickableTPRecord(ActionMessage a, CommandSender player){
		if(Bukkit.getServer().getName().equalsIgnoreCase("spigot")) {
			String[] message = Prism.messenger.playerMsg(a.getMessage());
			//line 1 holds the index so we set that as the highlighted for command click
			TextComponent[] toSend = new TextComponent[message.length];
			int i = 0;
			for (String m : message) {
				toSend[i] = new TextComponent(m);
				i++;
			}
			toSend[0].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
					new TextComponent("Click to  Teleport")}));
			toSend[0].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pr tp " + a.getResultIndex()));
			player.spigot().sendMessage(toSend);
		}else{
			player.sendMessage(Prism.messenger.playerMsg(a.getMessage()));
		}
	}

	public static void sendPageButtons(QueryResult results, CommandSender player){
		if (player instanceof Player) {
			if (results.getPage() == 1)
				player.spigot().sendMessage(MiscUtils.getNextButton());
			else if (results.getPage() < results.getTotal_pages())
				player.spigot().sendMessage(MiscUtils.getPrevNextButtons());
			else if (results.getPage() == results.getTotal_pages())
				player.spigot().sendMessage(MiscUtils.getPreviousButton());
		}
	}


}