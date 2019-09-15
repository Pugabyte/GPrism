package me.botsko.prism.actionlibs;

import me.botsko.prism.Prism;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class Ignore {
	private final Prism plugin;
	private final List<String> ignore_players;
	private final boolean ignore_players_whitelist;
	private final List<String> ignore_worlds;
	private final boolean ignore_worlds_whitelist;
	private final boolean ignore_creative;

	@SuppressWarnings("unchecked")
	public Ignore(Prism plugin) {
		this.plugin = plugin;
		ignore_players = (List<String>) plugin.getConfig().getList("prism.ignore.players");
		ignore_players_whitelist = plugin.getConfig().getBoolean("prism.ignore.players_whitelist");
		ignore_worlds = (List<String>) plugin.getConfig().getList("prism.ignore.worlds");
		ignore_worlds_whitelist = plugin.getConfig().getBoolean("prism.ignore.worlds_whitelist");
		ignore_creative = plugin.getConfig().getBoolean("prism.ignore.players-in-creative");
	}

	public boolean event(InternalActionType internalActionType) {
		return event(internalActionType.get());
	}

	public boolean event(ActionType actionType) {
		if (actionType.isPrism())
			return true;

		if (!actionType.isInternal())
			return true;

		return plugin.getConfig().getBoolean("prism.tracking." + actionType.getName());
	}

	public boolean event(InternalActionType internalActionType, World world, Player player) {
		return event(internalActionType.get(), world, player);
	}

	public boolean event(ActionType actionType, World world, Player player) {
		return event(actionType, world) && event(actionType, player);
	}

	public boolean event(InternalActionType internalActionType, Player player) {
		return event(internalActionType.get(), player);
	}

	public boolean event(ActionType actionType, Player player) {
		if (!event(actionType, player.getWorld())) {
			return false;
		}

		// Does the player have perms to ignore this action type?
		if (plugin.getConfig().getBoolean("prism.ignore.enable-perm-nodes")
				&& player.hasPermission("prism.ignore.tracking." + actionType.getName())) {
			Prism.debug("Player has permission node to ignore " + actionType.getName());
			return false;
		}

		return event(player);
	}

	public boolean event(Player player) {
		if (player == null) {
			Prism.debug("Player is being ignored because it is null");
			return false;
		}

		// Should we ignore this player?
		if (ignore_players != null && ignore_players.contains(player.getName()) != ignore_players_whitelist) {
			Prism.debug("Player is being ignored, per config: " + player.getName());
			return false;
		}

		// Should we ignore this player for being in creative?
		if (ignore_creative && player.getGameMode().equals(GameMode.CREATIVE)) {
			Prism.debug("Player is in creative mode, creative mode ignored: " + player.getName());
			return false;
		}
		return true;
	}

	public boolean event(InternalActionType internalActionType, Block block) {
		return event(internalActionType.get(), block);
	}

	public boolean event(ActionType actionType, Block block) {
		return event(actionType, block.getWorld());
	}

	public boolean event(InternalActionType internalActionType, World world) {
		return event(internalActionType.get(), world);
	}

	public boolean event(ActionType actionType, World world) {
		// Should we ignore this world?
		if (ignore_worlds != null && ignore_worlds.contains(world.getName()) != ignore_worlds_whitelist) {
			Prism.debug("World is being ignored, per config: " + world.getName());
			return false;
		}

		return event(actionType);

	}

}