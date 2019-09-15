package me.botsko.prism.listeners;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionFactory;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.InternalActionType;
import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.utils.BlockUtils;
import me.botsko.prism.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.List;

// TODO: Add support for concrete powder, when necessary events are implemented in Bukkit
public class PrismBlockEvents implements Listener {
	private final Prism plugin;

	public PrismBlockEvents(Prism plugin) {
		this.plugin = plugin;
	}

	/**
	 * If this is a container we need to trigger item removal for everything in
	 * it. It's important we record this *after* the block break so the log
	 * shows what really happened.
	 */
	public void logItemRemoveFromDestroyedContainer(String player_name, Block block) {
		if (block.getType().equals(Material.JUKEBOX)) {
			final Jukebox jukebox = (Jukebox) block.getState();
			final Material playing = jukebox.getPlaying();
			if (playing == null || playing.equals(Material.AIR))
				return;
			final ItemStack i = new ItemStack(jukebox.getPlaying(), 1);
			RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ITEM_REMOVE, i, i.getAmount(), 0, null,
					block.getLocation(), player_name));
			return;
		}
		if (block.getState() instanceof InventoryHolder) {
			final InventoryHolder container = (InventoryHolder) block.getState();
			int slot = 0;
			for (final ItemStack i : container.getInventory().getContents()) {
				// when double chests are broken, they record *all* contents
				// even though only half of the chest breaks.
				if ((block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST))
						&& slot > 26)
					break;
				// record item
				if (i != null) {
					RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ITEM_REMOVE, i, i.getAmount(), slot, null,
							block.getLocation(), player_name));
				}
				slot++;
			}
		}
	}

	protected void logBlockRelationshipsForBlock(String playername, Block block) {

		if (Tag.DOORS.isTagged(block.getType())) {
			return;
		}

		// Find a list of all blocks above this block that we know will fall.
		final ArrayList<Block> falling_blocks = BlockUtils.findFallingBlocksAboveBlock(block);
		if (falling_blocks.size() > 0) {
			for (final Block b : falling_blocks) {
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_FALL, b, playername));
			}
		}

		// Some blocks will essentially never have attachments - not
		// even worth spending time looking for them.
		// SUGAR CANE is not a solid but does have top face attached
		if (!block.getType().isSolid() && !block.getType().equals(Material.SUGAR_CANE)) {
			return;
		}

		// if it's a piston, the base will break without a physics events
		if (block.getType().equals(Material.PISTON_HEAD)) {
			final ArrayList<Block> pistonBases = BlockUtils.findSideFaceAttachedBlocks(block);
			if (pistonBases.size() > 0) {
				for (final Block p : pistonBases) {
					RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_BREAK, p, playername));
				}
			}
		}

		// Find a list of side-face attached blocks that will detach
		ArrayList<Block> detached_blocks = BlockUtils.findSideFaceAttachedBlocks(block);
		if (detached_blocks.size() > 0) {
			for (final Block b : detached_blocks) {
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_BREAK, b, playername));
			}
		}

		// Find a list of top-side attached blocks that will detach
		detached_blocks = BlockUtils.findTopFaceAttachedBlocks(block);
		if (detached_blocks.size() > 0) {
			for (final Block b : detached_blocks) {
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_BREAK, b, playername));
			}
		}

		// Find a list of all hanging entities on this block
		final ArrayList<Hanging> hanging = BlockUtils.findAttachedHangingEntities(block);
		if (hanging.size() > 0) {
			for (final Hanging e : hanging) {
				final String coord_key = e.getLocation().getBlockX() + ":" + e.getLocation().getBlockY() + ":"
						+ e.getLocation().getBlockZ();
				plugin.preplannedBlockFalls.put(coord_key, playername);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {

		final Player player = event.getPlayer();
		Block block = event.getBlock();

		if (block.getType().equals(Material.AIR))
			return;

		// Run ore find alerts
		if (!player.hasPermission("prism.alerts.ores.ignore") && !player.hasPermission("prism.alerts.ignore")) {
			plugin.oreMonitor.processAlertsFromBlock(player, block);
		}

		if (!Prism.getIgnore().event(InternalActionType.BLOCK_BREAK, player))
			return;

		// Change handling a bit if it's a long block
		final Block sibling = BlockUtils.getSiblingForDoubleLengthBlock(block);
		if (sibling != null && !block.getType().equals(Material.CHEST)
				&& !block.getType().equals(Material.TRAPPED_CHEST)) {
			block = sibling;
		}

		// log items removed from container
		// note: done before the container so a "rewind" for rollback will work
		// properly
		logItemRemoveFromDestroyedContainer(player.getName(), block);

		// check for block relationships
		// must be done before root block is broken, for rollbacks to work properly
		logBlockRelationshipsForBlock(player.getName(), block);

		RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_BREAK, block, player.getName()));

		// if obsidian, log portal blocks
		if (block.getType().equals(Material.OBSIDIAN)) {
			final ArrayList<Block> blocks = BlockUtils.findConnectedBlocksOfType(Material.NETHER_PORTAL,
					block, null);
			if (!blocks.isEmpty()) {
				// Only log 1 portal break, we don't need all 8
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_BREAK, blocks.get(0), player.getName()));
			}
		}

		// Pass to the break alerter
		if (!player.hasPermission("prism.alerts.use.break.ignore") && !player.hasPermission("prism.alerts.ignore")) {
			plugin.useMonitor.alertOnBlockBreak(player, event.getBlock());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {

		final Player player = event.getPlayer();
		final Block block = event.getBlock();

		if (!Prism.getIgnore().event(InternalActionType.BLOCK_PLACE, player))
			return;

		if (block.getType().equals(Material.AIR))
			return;

		final BlockState s = event.getBlockReplacedState();

		if (MaterialTag.BANNERS.isTagged(block.getType()) || MaterialTag.SKULLS.isTagged(block.getType()) || MaterialTag.BEDS.isTagged(block.getType())) {
			// Record full item data
			RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_PLACE, block, player.getName()));
		} else {
			// Record partial item data
			RecordingQueue.addToQueue(ActionFactory.createBlockChange(InternalActionType.BLOCK_PLACE, block.getLocation(), s.getType(),
					s.getBlockData(), block.getType(), block.getBlockData(), player.getName()));
		}

		// Pass to the placement alerter
		if (!player.hasPermission("prism.alerts.use.place.ignore") && !player.hasPermission("prism.alerts.ignore")) {
			plugin.useMonitor.alertOnBlockPlacement(player, block);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockSpread(final BlockSpreadEvent event) {

		// If fire, do we track fire spread? If not, do we track block-spread
		InternalActionType actionType = InternalActionType.BLOCK_SPREAD;
		if (event.getNewState().getType().equals(Material.FIRE)) {
			if (!Prism.getIgnore().event(InternalActionType.FIRE_SPREAD))
				return;
			actionType = InternalActionType.FIRE_SPREAD;
		} else {
			if (!Prism.getIgnore().event(InternalActionType.BLOCK_SPREAD, event.getBlock()))
				return;
		}

		final Block b = event.getBlock();
		final BlockState s = event.getNewState();
		RecordingQueue.addToQueue(ActionFactory.createBlockChange(actionType, b.getLocation(), b.getType(), b.getBlockData(),
				s.getType(), s.getBlockData(), "Environment"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockForm(final BlockFormEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.BLOCK_FORM, event.getBlock()))
			return;
		final Block b = event.getBlock();
		final BlockState s = event.getNewState();

		// Frost walker ice is already handled by EntityBlockFormEvent
		if (b.getType() == Material.WATER)
			if (s.getType() == Material.FROSTED_ICE)
				return;

		RecordingQueue.addToQueue(ActionFactory.createBlockChange(InternalActionType.BLOCK_FORM, b.getLocation(), b.getType(), b.getBlockData(),
				s.getType(), s.getBlockData(), "Environment"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockFade(final BlockFadeEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.BLOCK_FADE, event.getBlock()))
			return;
		final Block b = event.getBlock();
		if (b.getType().equals(Material.FIRE))
			return;
		final BlockState s = event.getNewState();
		RecordingQueue.addToQueue(ActionFactory.createBlockChange(InternalActionType.BLOCK_FADE, b.getLocation(), b.getType(), b.getBlockData(),
				s.getType(), s.getBlockData(), "Environment"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeavesDecay(final LeavesDecayEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.LEAF_DECAY, event.getBlock()))
			return;
		RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.LEAF_DECAY, event.getBlock(), "Environment"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBurn(final BlockBurnEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.BLOCK_BURN, event.getBlock()))
			return;
		Block block = event.getBlock();
		RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_BURN, block, "Environment"));

		// Change handling a bit if it's a long block
		final Block sibling = BlockUtils.getSiblingForDoubleLengthBlock(block);
		if (sibling != null && !block.getType().equals(Material.CHEST)
				&& !block.getType().equals(Material.TRAPPED_CHEST)) {
			block = sibling;
		}

		// check for block relationships
		logBlockRelationshipsForBlock("Environment", block);

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(final SignChangeEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.SIGN_CHANGE, event.getPlayer()))
			return;
		if (event.getBlock().getState().getData() instanceof Sign) {
			RecordingQueue.addToQueue(ActionFactory.createSign(InternalActionType.SIGN_CHANGE, event.getBlock(), event.getLines(), event.getPlayer()));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSetFire(final BlockIgniteEvent event) {
		final Player player = event.getPlayer();
		boolean shouldAlert = false;
		boolean alertsEnabled = plugin.getConfig().getBoolean("prism.alerts.uses.lighter")
				&& !player.hasPermission("prism.alerts.use.lighter.ignore")
				&& !player.hasPermission("prism.alerts.ignore");

		InternalActionType cause = null;
		switch (event.getCause()) {
			case FIREBALL:
				cause = InternalActionType.FIREBALL;
				shouldAlert = true;
				break;
			case FLINT_AND_STEEL:
				cause = InternalActionType.LIGHTER;
				shouldAlert = true;
				break;
			case LAVA:
				cause = InternalActionType.LAVA_IGNITE;
				break;
			case LIGHTNING:
				cause = InternalActionType.LIGHTNING;
				break;
			default:
		}
		if (cause != null) {
			if (!Prism.getIgnore().event(cause, event.getBlock().getWorld()))
				return;

			if (player != null) {
				if (shouldAlert && alertsEnabled) {
					plugin.useMonitor.alertOnItemUse(player, "used a " + cause);
				}
			}

			RecordingQueue.addToQueue(ActionFactory.createBlock(cause, event.getBlock(), (player == null ? "Environment" : player.getName())));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockDispense(final BlockDispenseEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.BLOCK_DISPENSE))
			return;
		RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.BLOCK_DISPENSE, event.getItem(),
				event.getItem().getAmount(), -1, null, event.getBlock().getLocation(), "dispenser"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonExtend(final BlockPistonExtendEvent event) {

		if (plugin.getConfig().getBoolean("prism.alerts.vanilla-xray.enabled")) {
			final Block noPlayer = event.getBlock().getRelative(event.getDirection())
					.getRelative(event.getDirection()).getRelative(BlockFace.DOWN);
			for (final Player pl : plugin.getServer().getOnlinePlayers()) {
				final Location loc = pl.getLocation();
				if (loc.getBlockX() == noPlayer.getX() && loc.getBlockY() == noPlayer.getY()
						&& loc.getBlockZ() == noPlayer.getZ()) {
					plugin.useMonitor.alertOnVanillaXray(pl, "possibly used a vanilla piston/xray trick");
					break;
				}
			}
		}

		if (!Prism.getIgnore().event(InternalActionType.BLOCK_SHIFT, event.getBlock()))
			return;

		final List<Block> blocks = event.getBlocks();
		if (!blocks.isEmpty()) {
			for (final Block block : blocks) {

				if (block.getType().equals(Material.AIR))
					continue;

				// Pistons move blocks to the block next to them. If nothing is
				// there it shows as air.
				// We should record the from coords, to coords, and block
				// replaced, as well as the block moved.
				RecordingQueue.addToQueue(ActionFactory.createBlockShift(InternalActionType.BLOCK_SHIFT, block,
						block.getRelative(event.getDirection()).getLocation(), "Piston"));

			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonRetract(final BlockPistonRetractEvent event) {
		if (!Prism.getIgnore().event(InternalActionType.BLOCK_SHIFT, event.getBlock()))
			return;
		if (!event.isSticky())
			return;
		final Block block = event.getBlock();
		if (block.getType().equals(Material.AIR))
			return;
		RecordingQueue.addToQueue(ActionFactory.createBlockShift(InternalActionType.BLOCK_SHIFT, event.getRetractLocation().getBlock(), block
				.getRelative(event.getDirection()).getLocation(), "Piston"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockFromTo(final BlockFromToEvent event) {
		// Ignore blocks that aren't liquid. @todo what else triggers this?
		if (!event.getBlock().isLiquid())
			return;

		final BlockState from = event.getBlock().getState();
		final BlockState to = event.getToBlock().getState();

		// Watch for blocks that the liquid can break
		if (BlockUtils.canFlowBreakMaterial(to.getType())) {
			if (from.getType() == Material.WATER) {
				if (Prism.getIgnore().event(InternalActionType.WATER_BREAK, event.getBlock())) {
					RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.WATER_BREAK, event.getToBlock(), "Water"));
				}
			} else if (from.getType() == Material.LAVA) {
				if (Prism.getIgnore().event(InternalActionType.LAVA_BREAK, event.getBlock())) {
					RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.LAVA_BREAK, event.getToBlock(), "Lava"));
				}
			}
		}

		// Record water flow
		if (from.getType() == Material.WATER) {
			if (Prism.getIgnore().event(InternalActionType.WATER_FLOW, event.getBlock())) {
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.WATER_FLOW, event.getBlock(), "Water"));
			}
		}

		// Record lava flow
		if (from.getType() == Material.LAVA) {
			if (Prism.getIgnore().event(InternalActionType.LAVA_FLOW, event.getBlock())) {
				RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.LAVA_FLOW, event.getBlock(), "Lava"));
			}
		}

        /*
          Predict the forming of Stone, Obsidian, Cobblestone because of
          lava/water flowing into each other. Boy, I wish bukkit used
          block_form for this.
         */
		if (!Prism.getIgnore().event(InternalActionType.BLOCK_FORM, event.getBlock()))
			return;

		// Lava flows to water. STONE forms
		if (from.getType().equals(Material.LAVA) && to.getType().equals(Material.WATER)) {
			final Block newTo = event.getToBlock();
			newTo.setType(Material.STONE);
			RecordingQueue.addToQueue(ActionFactory.createBlock(InternalActionType.BLOCK_FORM, newTo, "Environment"));
		}
	}

}