package me.botsko.prism.listeners;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionFactory;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.InternalActionType;
import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.actions.BlockAction;
import me.botsko.prism.actions.Handler;
import me.botsko.prism.players.PlayerIdentification;
import me.botsko.prism.utils.MaterialTag;
import me.botsko.prism.utils.MiscUtils;
import me.botsko.prism.wands.ProfileWand;
import me.botsko.prism.wands.Wand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PrismPlayerEvents implements Listener {
	private final Prism plugin;
	private final List<String> illegalCommands;
	private final List<String> ignoreCommands;

	@SuppressWarnings("unchecked")
	public PrismPlayerEvents(Prism plugin) {
		this.plugin = plugin;
		illegalCommands = (List<String>) plugin.getConfig().getList("prism.alerts.illegal-commands.commands");
		ignoreCommands = (List<String>) plugin.getConfig().getList("prism.do-not-track.commands");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {

		final Player player = event.getPlayer();
		final String cmd = event.getMessage().toLowerCase();

		final String[] cmdArgs = cmd.split(" ");
		final String primaryCmd = cmdArgs[0].substring(1);

		if (plugin.getConfig().getBoolean("prism.alerts.illegal-commands.enabled")) {
			if (illegalCommands.contains(primaryCmd)) {
				final String msg = player.getName() + " attempted an illegal command: " + primaryCmd + ". Originally: "
						+ cmd;
				player.sendMessage(Prism.messenger.playerError("Sorry, this command is not available in-game."));
				plugin.alertPlayers(null, msg);
				event.setCancelled(true);
				// Log to console
				if (plugin.getConfig().getBoolean("prism.alerts.illegal-commands.log-to-console")) {
					Prism.log(msg);
				}

				// Log to commands
				List<String> commands = plugin.getConfig().getStringList("prism.alerts.illegal-commands.log-commands");
				MiscUtils.dispatchAlert(msg, commands);
			}
		}

		if (!Prism.getIgnore().event(InternalActionType.PLAYER_COMMAND, player))
			return;

		// Ignore some commands based on config
		if (ignoreCommands.contains(primaryCmd)) {
			return;
		}

		RecordingQueue.addToQueue(ActionFactory.createPlayer(InternalActionType.PLAYER_COMMAND, player, event.getMessage()));

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		// Lookup player for cache reasons
		PlayerIdentification.cachePrismPlayer(player);

		// Track the join event
		if (!Prism.getIgnore().event(InternalActionType.PLAYER_JOIN, player))
			return;

		String ip = null;
		if (plugin.getConfig().getBoolean("prism.track-player-ip-on-join")) {
			ip = player.getAddress().getAddress().getHostAddress().toString();
		}

		RecordingQueue.addToQueue(ActionFactory.createPlayer(InternalActionType.PLAYER_JOIN, event.getPlayer(), ip));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(final PlayerQuitEvent event) {

		// Remove from primary key cache
		Prism.prismPlayers.remove(event.getPlayer().getName());

		// Track player quit
		if (!Prism.getIgnore().event(InternalActionType.PLAYER_QUIT, event.getPlayer()))
			return;

		RecordingQueue.addToQueue(ActionFactory.createPlayer(InternalActionType.PLAYER_QUIT, event.getPlayer(), null));

		// Remove any active wands for this player
		if (Prism.playersWithActiveTools.containsKey(event.getPlayer().getName())) {
			Prism.playersWithActiveTools.remove(event.getPlayer().getName());
		}
		// Remove any active previews for this player, even though they would
		// expire
		// naturally.
		if (plugin.playerActivePreviews.containsKey(event.getPlayer().getName())) {
			plugin.playerActivePreviews.remove(event.getPlayer().getName());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(final AsyncPlayerChatEvent event) {

		if (!Prism.getIgnore().event(InternalActionType.PLAYER_CHAT, event.getPlayer()))
			return;

		Plugin herochat = Prism.getInstance().getServer().getPluginManager().getPlugin("Herochat");
		if (herochat != null && herochat.isEnabled())
			return;

		RecordingQueue.addToQueue(ActionFactory.createPlayer(InternalActionType.PLAYER_CHAT, event.getPlayer(), event.getMessage()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.ITEM_DROP, event.getPlayer()))
			return;
		RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ITEM_DROP, event.getItemDrop().getItemStack(), event
				.getItemDrop().getItemStack().getAmount(), -1, null, event.getPlayer().getLocation(), event.getPlayer()
				.getName()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.ITEM_PICKUP, event.getPlayer()))
			return;
		RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ITEM_PICKUP, event.getItem().getItemStack(), event.getItem()
				.getItemStack().getAmount(), -1, null, event.getPlayer().getLocation(), event.getPlayer().getName()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerExpChangeEvent(final PlayerExpChangeEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.XP_PICKUP, event.getPlayer()))
			return;
		RecordingQueue.addToQueue(ActionFactory.createPlayer(InternalActionType.XP_PICKUP, event.getPlayer(), "" + event.getAmount()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
		final Player player = event.getPlayer();
		final InternalActionType cause = (event.getBucket() == Material.LAVA_BUCKET ? InternalActionType.LAVA_BUCKET : InternalActionType.WATER_BUCKET);

		if (!Prism.getIgnore().event(cause, player))
			return;

		final Block spot = event.getBlockClicked().getRelative(event.getBlockFace());
		final Material newType = (cause.equals(InternalActionType.LAVA_BUCKET) ? Material.LAVA : Material.WATER);
		RecordingQueue.addToQueue(ActionFactory.createBlockChange(cause, spot.getLocation(), spot.getType(), spot.getBlockData(),
				newType, Bukkit.createBlockData(newType), player.getName()));

		if (plugin.getConfig().getBoolean("prism.alerts.uses.lava") && event.getBucket() == Material.LAVA_BUCKET
				&& !player.hasPermission("prism.alerts.use.lavabucket.ignore")
				&& !player.hasPermission("prism.alerts.ignore")) {
			plugin.useMonitor.alertOnItemUse(player, "poured lava");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event) {

		final Player player = event.getPlayer();
		if (!Prism.getIgnore().event(InternalActionType.BUCKET_FILL, player))
			return;
		final Block spot = event.getBlockClicked().getRelative(event.getBlockFace());

		String liquid_type = "milk";
		if (spot.getType().equals(Material.WATER)) {
			liquid_type = "water";
		} else if (spot.getType().equals(Material.LAVA)) {
			liquid_type = "lava";
		}

		final Handler pa = ActionFactory.createPlayer(InternalActionType.BUCKET_FILL, player, liquid_type);

		// Override the location with the area taken
		pa.setX(spot.getX());
		pa.setY(spot.getY());
		pa.setZ(spot.getZ());

		RecordingQueue.addToQueue(pa);

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.PLAYER_TELEPORT, event.getPlayer()))
			return;
		final TeleportCause c = event.getCause();
		if (c.equals(TeleportCause.END_PORTAL) || c.equals(TeleportCause.NETHER_PORTAL)
				|| c.equals(TeleportCause.ENDER_PEARL)) {
			RecordingQueue.addToQueue(ActionFactory.createEntityTravel(InternalActionType.PLAYER_TELEPORT, event.getPlayer(), event.getFrom(),
					event.getTo(), event.getCause()));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnchantItem(final EnchantItemEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.ENCHANT_ITEM, event.getEnchanter()))
			return;
		final Player player = event.getEnchanter();
		RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ENCHANT_ITEM, event.getItem(),
				event.getEnchantsToAdd(), event.getEnchantBlock().getLocation(), player));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraftItem(final CraftItemEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if (!Prism.getIgnore().event(InternalActionType.CRAFT_ITEM, player))
			return;
		final ItemStack item = event.getRecipe().getResult();
		RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.CRAFT_ITEM, item, 1, -1, null, player.getLocation(),
				player.getName()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		// Are they using a wand (or do we always allow it)
		if (Prism.playersWithActiveTools.containsKey(player.getName())) {

			final Wand wand = Prism.playersWithActiveTools.get(player.getName());

			// Does the player have such item?
			if (wand != null && player.getInventory().getItemInMainHand().getType() == wand.getItemType()) {

				if (event.getHand() == EquipmentSlot.OFF_HAND
						&& (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
					Prism.debug("Cancelling event for wand use.");
					event.setCancelled(true);
					player.updateInventory();
					return;
				}

				// Left click is for current block
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					wand.playerLeftClick(player, block.getLocation());
				}
				// Right click is for relative block on blockface
				// except block placements - those will be handled by the
				// blockplace.
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					block = block.getRelative(event.getBlockFace());
					wand.playerRightClick(player, block.getLocation());
				}

				if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
					Prism.debug("Cancelling event for wand use.");
					event.setCancelled(true);
					player.updateInventory();
					return;
				}
			}
		}

		if (event.useInteractedBlock() == Event.Result.DENY)
			return;

		// Doors, buttons, containers, etc may only be opened with a right-click
		// as of 1.4
		if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			String coord_key;
			switch (block.getType()) {
				case FURNACE:
				case DISPENSER:
				case CHEST:
				case ENDER_CHEST:
				case ANVIL:
				case BREWING_STAND:
				case TRAPPED_CHEST:
				case HOPPER:
				case DROPPER:
					// @todo tag containers
					if (!Prism.getIgnore().event(InternalActionType.CONTAINER_ACCESS, player))
						return;
					RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.CONTAINER_ACCESS, block, player.getName()));
					break;
				case JUKEBOX:
					recordDiscInsert(block, event.getItem(), player);
					break;
				case CAKE:
					recordCakeEat(block, player);
					break;
				case JUNGLE_LOG:
					recordCocoaPlantEvent(block, event.getItem(), event.getBlockFace(), player);
					break;
				case ACACIA_DOOR:
				case BIRCH_DOOR:
				case DARK_OAK_DOOR:
				case JUNGLE_DOOR:
				case OAK_DOOR:
				case SPRUCE_DOOR:
				case ACACIA_TRAPDOOR:
				case BIRCH_TRAPDOOR:
				case DARK_OAK_TRAPDOOR:
				case JUNGLE_TRAPDOOR:
				case OAK_TRAPDOOR:
				case SPRUCE_TRAPDOOR:
				case ACACIA_FENCE_GATE:
				case BIRCH_FENCE_GATE:
				case DARK_OAK_FENCE_GATE:
				case JUNGLE_FENCE_GATE:
				case OAK_FENCE_GATE:
				case SPRUCE_FENCE_GATE:
				case ACACIA_BUTTON:
				case BIRCH_BUTTON:
				case DARK_OAK_BUTTON:
				case JUNGLE_BUTTON:
				case OAK_BUTTON:
				case SPRUCE_BUTTON:
				case STONE_BUTTON:
				case LEVER:
					if (!Prism.getIgnore().event(InternalActionType.BLOCK_USE, player))
						return;
					RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_USE, block, player.getName()));
					break;
				case WHEAT:
				case GRASS:
				case MELON_STEM:
				case PUMPKIN_STEM:
				case SPRUCE_SAPLING:
				case ACACIA_SAPLING:
				case BAMBOO_SAPLING:
				case BIRCH_SAPLING:
				case DARK_OAK_SAPLING:
				case JUNGLE_SAPLING:
				case OAK_SAPLING:
				case CARROT:
				case POTATO:
				case BEETROOT:
					recordBonemealEvent(block, event.getItem(), event.getBlockFace(), player);
					break;
				case RAIL:
				case DETECTOR_RAIL:
				case POWERED_RAIL:
				case ACTIVATOR_RAIL:
					coord_key = block.getX() + ":" + block.getY() + ":" + block.getZ();
					plugin.preplannedVehiclePlacement.put(coord_key, player);
					break;
				case TNT:
					if (event.getItem() != null && event.getItem().getType().equals(Material.FLINT_AND_STEEL)) {
						if (!Prism.getIgnore().event(InternalActionType.TNT_PRIME, player))
							return;
						RecordingQueue.addToQueue(ActionFactory.createUse(InternalActionType.TNT_PRIME, block.getType(), block, player));
					}
					break;
				default:
					break;
			}

			// if they're holding a spawner egg
			if (event.getItem() != null && MaterialTag.SPAWN_EGGS.isTagged(event.getItem().getType())) {
				recordMonsterEggUse(block, event.getItem(), player);
			}

			// if they're holding a rocket
			if (event.getItem() != null && event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
				recordRocketLaunch(block, event.getItem(), event.getBlockFace(), player);
			}

			// if they're holding a boat (why they hell can you put boats on
			// anything...)
			if (event.getItem() != null && MaterialTag.BOATS.isTagged(event.getItem().getType())) {
				coord_key = block.getX() + ":" + (block.getY() + 1) + ":" + block.getZ();
				plugin.preplannedVehiclePlacement.put(coord_key, player);
			}
		}

		// Punching fire
		if (block != null && event.getAction() == Action.LEFT_CLICK_BLOCK) {
			final Block above = block.getRelative(BlockFace.UP);
			if (above.getType().equals(Material.FIRE)) {
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_BREAK, above, player.getName()));
			}
		}

		if (!plugin.getConfig().getBoolean("prism.tracking.crop-trample"))
			return;

		if (block != null && event.getAction() == Action.PHYSICAL) {
			if (block.getType() == Material.FARMLAND) { // They are stepping on
				// soil
				if (!Prism.getIgnore().event(InternalActionType.CROP_TRAMPLE, player))
					return;
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.CROP_TRAMPLE, block.getRelative(BlockFace.UP),
						player.getName()));
			}
		}
	}

	protected void recordCocoaPlantEvent(Block block, ItemStack inhand, BlockFace clickedFace, Player player) {
		if (!Prism.getIgnore().event(InternalActionType.BLOCK_PLACE, block) || inhand == null)
			return;
		if (block.getType().equals(Material.JUNGLE_LOG) && inhand.getType().equals(Material.COCOA_BEANS)) {
			final Location newLoc = block.getRelative(clickedFace).getLocation();
			final Block actualBlock = block.getWorld().getBlockAt(newLoc);
			// @todo this is a lame way to do this
			final BlockAction action = new BlockAction();
			action.setActionType(InternalActionType.BLOCK_PLACE.get());
			action.setPlayer(player);
			action.setLocation(actualBlock.getLocation());
			action.setMaterial(Material.COCOA_BEANS);
			RecordingQueue.addToQueue(action);
		}
	}

	protected void recordBonemealEvent(Block block, ItemStack inhand, BlockFace clickedFace, Player player) {
		if (inhand != null && inhand.getType().equals(Material.BONE_MEAL)) {
			if (!Prism.getIgnore().event(InternalActionType.BONEMEAL_USE, block))
				return;
			RecordingQueue.addToQueue(ActionFactory.createUse(InternalActionType.BONEMEAL_USE, Material.BONE_MEAL, block, player));
		}
	}

	protected void recordMonsterEggUse(Block block, ItemStack inhand, Player player) {
		if (!Prism.getIgnore().event(InternalActionType.SPAWNEGG_USE, block) || inhand == null)
			return;
		RecordingQueue.addToQueue(ActionFactory.createUse(InternalActionType.SPAWNEGG_USE, inhand.getType(), block, player));
	}

	protected void recordRocketLaunch(Block block, ItemStack inhand, BlockFace clickedFace, Player player) {
		if (!Prism.getIgnore().event(InternalActionType.FIREWORK_LAUNCH, block) || inhand == null)
			return;
		RecordingQueue
				.addToQueue(ActionFactory.createItemStack(InternalActionType.FIREWORK_LAUNCH, inhand, null, block.getLocation(), player));
	}

	protected void recordCakeEat(Block block, Player player) {
		if (!Prism.getIgnore().event(InternalActionType.CAKE_EAT, block))
			return;
		RecordingQueue.addToQueue(ActionFactory.createUse(InternalActionType.CAKE_EAT, Material.CAKE, block, player));
	}

	protected void recordDiscInsert(Block block, ItemStack inhand, Player player) {

		final Jukebox jukebox = (Jukebox) block.getState();

		// Do we have a disc inside? This will pop it out
		if (!jukebox.getPlaying().equals(Material.AIR)) {

			// Record currently playing disc
			final ItemStack i = new ItemStack(jukebox.getPlaying(), 1);
			RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ITEM_REMOVE, i, i.getAmount(), 0, null,
					block.getLocation(), player.getName()));

		} else {

			// They have to be holding a record
			if (inhand == null || !inhand.getType().isRecord())
				return;

			// Record the insert
			RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ITEM_INSERT, inhand, 1, 0, null,
					block.getLocation(), player.getName()));

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerEntityInteract(final PlayerInteractEntityEvent event) {

		final Player player = event.getPlayer();
		final Entity entity = event.getRightClicked();

		// Are they using a wand?
		if (Prism.playersWithActiveTools.containsKey(player.getName())) {

			// Pull the wand in use
			final Wand wand = Prism.playersWithActiveTools.get(player.getName());
			if (wand != null && wand instanceof ProfileWand) {

				wand.playerRightClick(player, entity);

				// Always cancel
				event.setCancelled(true);

			}
		}
	}

}
