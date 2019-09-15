package me.botsko.prism.utils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {

	public static boolean isHolding(Player player, Material... materials) {
		PlayerInventory inv = player.getInventory();
		for (Material material : materials) {
			if (inv.getItemInMainHand().getType().equals(material) || inv.getItemInOffHand().getType().equals(material))
				return true;
		}
		return false;
	}

	public static boolean isHolding(Player player, MaterialTag materialTag) {
		PlayerInventory inv = player.getInventory();
		return materialTag.isTagged(inv.getItemInMainHand().getType()) || materialTag.isTagged(inv.getItemInOffHand().getType());
	}

	public static void updateInventory(Player p) {
		p.updateInventory();
	}

	public static boolean playerInvIsEmpty(Player p) {
		for (ItemStack item : p.getInventory().getContents()) {
			if (item != null)
				return false;
		}
		return true;
	}

	public static ItemStack getEquipment(EntityEquipment equipment, EquipmentSlot slot) {
		switch(slot) {
			case HAND:
				return equipment.getItemInMainHand();
			case OFF_HAND:
				return equipment.getItemInOffHand();
			case FEET:
				return equipment.getBoots();
			case LEGS:
				return equipment.getLeggings();
			case CHEST:
				return equipment.getChestplate();
			case HEAD:
				return equipment.getHelmet();
		}

		throw new IllegalArgumentException("EquipmentSlot " + slot.name() + " not recognised");
	}

	public static void setEquipment(EntityEquipment equipment, EquipmentSlot slot, ItemStack item) {
		switch(slot) {
			case HAND:
				equipment.setItemInMainHand(item);
				break;
			case OFF_HAND:
				equipment.setItemInOffHand(item);
				break;
			case FEET:
				equipment.setBoots(item);
				break;
			case LEGS:
				equipment.setLeggings(item);
				break;
			case CHEST:
				equipment.setChestplate(item);
				break;
			case HEAD:
				equipment.setHelmet(item);
				break;
		}
	}

	private static final EnumMap<Material, EquipmentSlot> slots = new EnumMap<>(Material.class);
	static {
		slots.put(Material.LEATHER_BOOTS, EquipmentSlot.FEET);
		slots.put(Material.CHAINMAIL_BOOTS, EquipmentSlot.FEET);
		slots.put(Material.IRON_BOOTS, EquipmentSlot.FEET);
		slots.put(Material.GOLDEN_BOOTS, EquipmentSlot.FEET);
		slots.put(Material.DIAMOND_BOOTS, EquipmentSlot.FEET);

		slots.put(Material.LEATHER_LEGGINGS, EquipmentSlot.LEGS);
		slots.put(Material.CHAINMAIL_LEGGINGS, EquipmentSlot.LEGS);
		slots.put(Material.IRON_LEGGINGS, EquipmentSlot.LEGS);
		slots.put(Material.GOLDEN_LEGGINGS, EquipmentSlot.LEGS);
		slots.put(Material.DIAMOND_LEGGINGS, EquipmentSlot.LEGS);

		slots.put(Material.LEATHER_CHESTPLATE, EquipmentSlot.CHEST);
		slots.put(Material.CHAINMAIL_CHESTPLATE, EquipmentSlot.CHEST);
		slots.put(Material.IRON_CHESTPLATE, EquipmentSlot.CHEST);
		slots.put(Material.GOLDEN_CHESTPLATE, EquipmentSlot.CHEST);
		slots.put(Material.DIAMOND_CHESTPLATE, EquipmentSlot.CHEST);

		slots.put(Material.LEATHER_HELMET, EquipmentSlot.HEAD);
		slots.put(Material.CHAINMAIL_HELMET, EquipmentSlot.HEAD);
		slots.put(Material.IRON_HELMET, EquipmentSlot.HEAD);
		slots.put(Material.GOLDEN_HELMET, EquipmentSlot.HEAD);
		slots.put(Material.DIAMOND_HELMET, EquipmentSlot.HEAD);

		slots.put(Material.SKELETON_SKULL, EquipmentSlot.HEAD);
		slots.put(Material.WITHER_SKELETON_SKULL, EquipmentSlot.HEAD);
		slots.put(Material.CREEPER_HEAD, EquipmentSlot.HEAD);
		slots.put(Material.DRAGON_HEAD, EquipmentSlot.HEAD);
		slots.put(Material.PLAYER_HEAD, EquipmentSlot.HEAD);
		slots.put(Material.ZOMBIE_HEAD, EquipmentSlot.HEAD);
		slots.put(Material.CARVED_PUMPKIN, EquipmentSlot.HEAD);
		slots.put(Material.TURTLE_HELMET, EquipmentSlot.HEAD);

	}
	public static EquipmentSlot getTargetArmorSlot(Material material) {
		return slots.getOrDefault(material, EquipmentSlot.HAND);
	}

	public static boolean playerArmorIsEmpty(Player p) {
		for (ItemStack item : p.getInventory().getArmorContents()) {
			if (item != null && !item.getType().equals(Material.AIR))
				return false;
		}
		return true;
	}

	public static int inventoryHasItem(Inventory inv, Material material) {
		int currentSlot = 0;
		for (ItemStack item : inv.getContents()) {
			if (item != null && item.getType() == material) {
				return currentSlot;
			}
			currentSlot++;
		}
		return -1;
	}

	public static ItemStack extractItemsMatchingHeldItemFromPlayer(Player player, int desiredQuantity) {

		if (player == null || !ItemUtils.isValidItem(player.getInventory().getItemInMainHand())) {
			throw new IllegalArgumentException("Invalid player or invalid held item.");
		}

		int quantityFound = 0;
		ItemStack itemDefinition = player.getInventory().getItemInMainHand().clone();

		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			ItemStack item = player.getInventory().getItem(slot);
			if (item == null)
				continue;
			if (ItemUtils.equals(item, itemDefinition)) {

				// check how many items we need
				int diff = desiredQuantity - quantityFound;

				// Consume whole stack
				if (diff > item.getAmount()) {
					quantityFound += item.getAmount();
					player.getInventory().clear(slot);
				}
				// Only need a portion
				else {
					quantityFound += diff;
					item.setAmount(item.getAmount() - diff);
					player.getInventory().setItem(slot, item);
				}
			}
			if (desiredQuantity == quantityFound)
				break;
		}

		itemDefinition.setAmount(quantityFound);

		return itemDefinition;

	}

	public static boolean moveItemToHand(PlayerInventory inv, Material material) {
		int slot = inventoryHasItem(inv, material);
		if (slot > -1) {
			ItemStack item = inv.getItem(slot);
			inv.clear(slot);
			// If the player has an item in-hand, switch to a vacant spot
			if (!playerHasEmptyHand(inv)) {
				inv.setItem(slot, inv.getItemInMainHand());
			}
			inv.setItemInMainHand(item);
			return true;
		}
		return false;
	}

	public static boolean playerHasEmptyHand(PlayerInventory inv) {
		return (inv.getItemInMainHand().getType() == Material.AIR);
	}

	public static HashMap<Integer, ItemStack> addItemToInventory(Inventory inv, ItemStack item) {
		return inv.addItem(item);
	}

	public static boolean handItemToPlayer(PlayerInventory inv, ItemStack item) {
		// Ensure there's at least one empty inv spot
		if (inv.firstEmpty() != -1) {
			ItemStack originalItem = inv.getItemInMainHand().clone();
			// If the player has an item in-hand, switch to a vacant spot
			if (!playerHasEmptyHand(inv)) {
				// We need to manually add the item stack to a different
				// slot because by default, bukkit combines items with addItem
				// and that was causing items to be lost unless they were the max
				// stack size
				for (int i = 0; i <= inv.getSize(); i++) {
					if (i == inv.getHeldItemSlot())
						continue;
					ItemStack current = inv.getItem(i);
					if (current == null) {
						inv.setItem(i, originalItem);
						break;
					}
				}
			}
			inv.setItemInMainHand(item);
			return true;
		}
		return false;
	}

	public static void subtractAmountFromPlayerInvSlot(PlayerInventory inv, int slot, int quant) {
		ItemStack itemAtSlot = inv.getItem(slot);
		if (itemAtSlot != null && quant <= 64) {
			itemAtSlot.setAmount(itemAtSlot.getAmount() - quant);
			if (itemAtSlot.getAmount() == 0) {
				inv.clear(slot);
			}
		}
	}

	public static void dropItemsByPlayer(HashMap<Integer, ItemStack> leftovers, Player player) {
		if (!leftovers.isEmpty()) {
			for (Entry<Integer, ItemStack> entry : leftovers.entrySet()) {
				player.getWorld().dropItemNaturally(player.getLocation(), entry.getValue());
			}
		}
	}

	public static boolean isEmpty(Inventory in) {
		boolean ret = false;
		if (in == null) {
			return true;
		}
		for (ItemStack item : in.getContents()) {
			ret |= (item != null);
		}
		return !ret;
	}
}