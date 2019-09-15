package me.botsko.prism.listeners;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionFactory;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.InternalActionType;
import me.botsko.prism.actionlibs.RecordingQueue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Map.Entry;

public class PrismInventoryEvents implements Listener {
	private final Prism plugin;

	public PrismInventoryEvents(Prism plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryPickupItem(final InventoryPickupItemEvent event) {

		if (!plugin.getConfig().getBoolean("prism.track-hopper-item-events"))
			return;

		if (!Prism.getIgnore().event(InternalActionType.ITEM_PICKUP.get()))
			return;

		// If hopper
		if (event.getInventory().getType().equals(InventoryType.HOPPER)) {
			RecordingQueue.addToQueue(ActionFactory.createItemStack(InternalActionType.ITEM_PICKUP.get(), event.getItem().getItemStack(), event
					.getItem().getItemStack().getAmount(), -1, null, event.getItem().getLocation(), "hopper"));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryDrag(final InventoryDragEvent event) {

		if (!plugin.getConfig().getBoolean("prism.tracking.item-insert")
				&& !plugin.getConfig().getBoolean("prism.tracking.item-remove"))
			return;

		// Get container
		final InventoryHolder ih = event.getInventory().getHolder();
		Location containerLoc = null;
		if (ih instanceof BlockState) {
			final BlockState eventChest = (BlockState) ih;
			containerLoc = eventChest.getLocation();
		}

		// Store some info
		final Player player = (Player) event.getWhoClicked();

		final Map<Integer, ItemStack> newItems = event.getNewItems();
		for (final Entry<Integer, ItemStack> entry : newItems.entrySet()) {
			recordInvAction(player, containerLoc, entry.getValue(), entry.getKey(), InternalActionType.ITEM_INSERT.get());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(final InventoryClickEvent event) {

		if (!plugin.getConfig().getBoolean("prism.tracking.item-insert")
				&& !plugin.getConfig().getBoolean("prism.tracking.item-remove"))
			return;

		Location containerLoc = null;

		// Store some info
		final Player player = (Player) event.getWhoClicked();
		final ItemStack currentitem = event.getCurrentItem();
		final ItemStack cursoritem = event.getCursor();

		// Get location
		if (event.getInventory().getHolder() instanceof BlockState) {
			final BlockState b = (BlockState) event.getInventory().getHolder();
			containerLoc = b.getLocation();
		} else if (event.getInventory().getHolder() instanceof Entity) {
			final Entity e = (Entity) event.getInventory().getHolder();
			containerLoc = e.getLocation();
		} else if (event.getInventory().getHolder() instanceof DoubleChest) {
			final DoubleChest chest = (DoubleChest) event.getInventory().getHolder();
			containerLoc = chest.getLocation();
		}

		// Double chests report 27 default size, though they actually
		// have 6 rows of 9 for 54 slots
		int defaultSize = event.getView().getType().getDefaultSize();
		if (event.getInventory().getHolder() instanceof DoubleChest) {
			defaultSize = event.getView().getType().getDefaultSize() * 2;
		}

		// Click in the block inventory produces slot/rawslot that are equal, only until the slot numbers exceed the
		// slot count of the inventory. At that point, they represent the player inv.
		if (event.getSlot() == event.getRawSlot() && event.getRawSlot() <= defaultSize) {
			ItemStack addStack = null;
			ItemStack removeStack = null;

			if (currentitem != null && !currentitem.getType().equals(Material.AIR) && cursoritem != null
					&& !cursoritem.getType().equals(Material.AIR)) {
				// If BOTH items are not air then you've swapped an item. We need to
				// record an insert for the cursor item and
				// and remove for the current.

				if (currentitem.isSimilar(cursoritem)) {
					// Items are similar enough to stack
					int amount = cursoritem.getAmount();

					if (event.isRightClick()) {
						amount = 1;
					}

					int remaining = (currentitem.getMaxStackSize() - currentitem.getAmount());
					int inserted = Math.min(amount, remaining);

					if (inserted > 0) {
						addStack = cursoritem.clone();
						addStack.setAmount(inserted);
					}
				} else {
					// Items are not similar
					addStack = cursoritem.clone();
					removeStack = currentitem.clone();
				}
			} else if (currentitem != null && !currentitem.getType().equals(Material.AIR)) {
				removeStack = currentitem.clone();
			} else if (cursoritem != null && !cursoritem.getType().equals(Material.AIR)) {
				addStack = cursoritem.clone();
			}

			// Record events
			if (addStack != null) {
				recordInvAction(player, containerLoc, addStack, event.getRawSlot(), InternalActionType.ITEM_INSERT.get(), event);
			}
			if (removeStack != null) {
				recordInvAction(player, containerLoc, removeStack, event.getRawSlot(), InternalActionType.ITEM_REMOVE.get(), event);
			}
			return;
		}
		if (event.isShiftClick() && cursoritem != null && cursoritem.getType().equals(Material.AIR)) {
			recordInvAction(player, containerLoc, currentitem, -1, InternalActionType.ITEM_INSERT.get(), event);
		}
	}

	protected void recordInvAction(Player player, Location containerLoc, ItemStack item, int slot, ActionType actionType) {
		recordInvAction(player, containerLoc, item, slot, actionType, null);
	}

	protected void recordInvAction(Player player, Location containerLoc, ItemStack item, int slot, ActionType actionType,
								   InventoryClickEvent event) {
		if (!Prism.getIgnore().event(actionType, player))
			return;

		// Determine correct quantity. Right-click events change the item
		// quantity but don't seem to update the cursor/current items.
		int officialQuantity = 0;
		if (item != null) {
			officialQuantity = item.getAmount();
			// If the player right-clicked we need to assume the amount
			if (event != null && event.isRightClick()) {
				// If you're right-clicking to remove an item, it divides by two
				if (actionType.equals(InternalActionType.ITEM_REMOVE.get())) {
					officialQuantity = (officialQuantity - (int) Math.floor((item.getAmount() / 2)));
				}
				// If you're right-clicking to insert, it's only one
				else if (actionType.equals(InternalActionType.ITEM_INSERT.get())) {
					officialQuantity = 1;
				}
			}
		}

		// Record it!
		if (actionType != null && containerLoc != null && item != null && !item.getType().equals(Material.AIR)  && officialQuantity > 0) {
			RecordingQueue.addToQueue(ActionFactory.createItemStack(actionType, item, officialQuantity, slot, null,
					containerLoc, player.getName()));
		}
	}

}
