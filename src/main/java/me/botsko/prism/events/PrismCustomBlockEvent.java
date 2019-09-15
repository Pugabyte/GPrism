package me.botsko.prism.events;

import me.botsko.prism.actionlibs.ActionType;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class PrismCustomBlockEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final String plugin_name;
	private final ActionType actionType;
	private final Player player;
	private final Block block;
	private final String message;

	public PrismCustomBlockEvent(Plugin plugin, ActionType actionType, Player player, Block block, String message) {
		this.plugin_name = plugin.getName();
		this.actionType = actionType;
		this.player = player;
		this.block = block;
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

	public Player getPlayer() {
		return player;
	}

	public String getMessage() {
		return message;
	}

	public Block getBlock() {
		return block;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}