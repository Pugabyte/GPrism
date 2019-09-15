package me.botsko.prism.monitors;

import me.botsko.prism.Prism;
import me.botsko.prism.utils.ItemUtils;
import me.botsko.prism.utils.MiscUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UseMonitor {
	protected final ArrayList<String> blocksToAlertOnPlace;
	protected final ArrayList<String> blocksToAlertOnBreak;
	private final Prism plugin;
	private ConcurrentHashMap<String, Integer> countedEvents = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public UseMonitor(Prism plugin) {
		this.plugin = plugin;
		blocksToAlertOnPlace = (ArrayList<String>) plugin.getConfig().getList("prism.alerts.uses.item-placement");
		blocksToAlertOnBreak = (ArrayList<String>) plugin.getConfig().getList("prism.alerts.uses.item-break");
		resetEventsQueue();
	}

	protected void incrementCount(String playername, String msg) {
		int count = 0;
		if (countedEvents.containsKey(playername)) {
			count = countedEvents.get(playername);
		}
		count++;
		countedEvents.put(playername, count);

		msg = ChatColor.GRAY + playername + " " + msg;
		if (count == 5) {
			msg = playername + " continues - pausing warnings.";
		}

		if (count <= 5) {
			if (plugin.getConfig().getBoolean("prism.alerts.uses.log-to-console")) {
				plugin.alertPlayers(null, msg);
				Prism.log(msg);
			}

			// Log to commands
			List<String> commands = plugin.getConfig().getStringList("prism.alerts.uses.log-commands");
			MiscUtils.dispatchAlert(msg, commands);
		}
	}

	protected boolean checkFeatureShouldProceed(Player player) {
		if (!plugin.getConfig().getBoolean("prism.alerts.uses.enabled")) return false;

		// Ignore players who would see the alerts
		if (plugin.getConfig().getBoolean("prism.alerts.uses.ignore-staff") && player.hasPermission("prism.alerts"))
			return false;

		// Ignore certain ranks
		if (player.hasPermission("prism.bypass-use-alerts")) return false;

		return true;
	}

	public void alertOnBlockPlacement(Player player, Block block) {
		if (!checkFeatureShouldProceed(player)) return;

		if (blocksToAlertOnPlace.contains(block.getType().name())) {
			incrementCount(player.getName(), "placed " + ItemUtils.getNiceName(block.getType()));
		}
	}

	public void alertOnBlockBreak(Player player, Block block) {
		if (!checkFeatureShouldProceed(player)) return;

		if (blocksToAlertOnBreak.contains(block.getType().name())) {
			incrementCount(player.getName(), "broke " + ItemUtils.getNiceName(block.getType()));
		}
	}

	public void alertOnItemUse(Player player, String use_msg) {
		if (!checkFeatureShouldProceed(player)) return;

		incrementCount(player.getName(), use_msg);
	}

	public void alertOnVanillaXray(Player player, String use_msg) {
		if (!checkFeatureShouldProceed(player)) return;

		incrementCount(player.getName(), use_msg);
	}

	/**
	 * Reset the queue every now and then Technically this can reset someone's
	 * counts too early but that just means staff will see extra warnings.
	 */
	public void resetEventsQueue() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				countedEvents = new ConcurrentHashMap<>();
			}
		}, 7000L, 7000L);
	}

}