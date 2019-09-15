package me.botsko.prism.listeners;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionFactory;
import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.events.PrismCustomBlockEvent;
import me.botsko.prism.events.PrismCustomPlayerActionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class PrismCustomEvents implements Listener {
	private final Prism plugin;

	public PrismCustomEvents(Prism plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCustomPlayerAction(final PrismCustomPlayerActionEvent event) {
		final ArrayList<String> allowedPlugins = (ArrayList<String>) plugin.getConfig().getList("prism.tracking.api.allowed-plugins");
		if (allowedPlugins.contains(event.getPluginName())) {
			RecordingQueue.addToQueue(ActionFactory.createPlayer(event.getActionType(), event.getPlayer(),
					event.getMessage()));
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCustomBlockAction(final PrismCustomBlockEvent event) {
		final ArrayList<String> allowedPlugins = (ArrayList<String>) plugin.getConfig().getList(
				"prism.tracking.api.allowed-plugins");
		if (allowedPlugins.contains(event.getPluginName())) {
			RecordingQueue.addToQueue(ActionFactory.createBlock(event.getActionType(), event.getBlock(),
					event.getPlayer().getName()));
		}
	}

}