package me.botsko.prism.events;

import me.botsko.prism.actionlibs.ActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class PrismCustomPlayerActionEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final String plugin_name;
	private final ActionType actionType;
	private final Player player;
	private final String message;

	public PrismCustomPlayerActionEvent(Plugin plugin, ActionType actionType, Player player, String message) {
		this.plugin_name = plugin.getName();
		this.actionType = actionType;
		this.player = player;
		this.message = message + ChatColor.GOLD + " [" + this.plugin_name + "]" + ChatColor.DARK_AQUA;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public String getPluginName() {
		return plugin_name;
	}

	public ActionType getActionType() {
		return actionType;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Required by bukkit for proper event handling.
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}