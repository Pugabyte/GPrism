package me.botsko.prism.wands;

import me.botsko.prism.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class WandBase {
	protected boolean item_given = false;
	protected String wand_mode;
	protected Material material = Material.AIR;
	protected ItemStack original_item;

	public void setItemWasGiven(boolean given) {
		this.item_given = given;
	}

	public boolean itemWasGiven() {
		return item_given;
	}

	public String getWandMode() {
		return wand_mode;
	}

	public void setWandMode(String mode) {
		wand_mode = mode;
	}

	public Material getItemType() {
		return material;
	}

	public void setItemType(Material material) {
		this.material = material;
	}

	public void setOriginallyHeldItem(ItemStack item) {
		if (!item.getType().equals(Material.AIR)) {
			original_item = item;
		}
	}

	public void disable(Player player) {
		final PlayerInventory inv = player.getInventory();
		if (itemWasGiven()) {
			int itemSlot;
			// Likely is what they're holding
			if (inv.getItemInMainHand().getType() == material) {
				itemSlot = inv.getHeldItemSlot();
			} else {
				itemSlot = InventoryUtils.inventoryHasItem(inv, material);
			}
			if (itemSlot > -1) {
				InventoryUtils.subtractAmountFromPlayerInvSlot(inv, itemSlot, 1);
				player.updateInventory();
			}
		}
		if (original_item != null) {
			InventoryUtils.moveItemToHand(inv, original_item.getType());
		}
	}

}
