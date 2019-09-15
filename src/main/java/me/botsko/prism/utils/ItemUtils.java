package me.botsko.prism.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

public class ItemUtils {

	@SuppressWarnings("RedundantIfStatement")
	public static boolean isAcceptableWand(Material material) {
		if (MaterialTag.LIQUIDS.isTagged(material)) return false;
		if (MaterialTag.PORTALS.isTagged(material)) return false;
		if (MaterialTag.SPAWN_EGGS.isTagged(material)) return false;
		if (Material.FIRE.equals(material)) return false;

		return true;
	}

	public static boolean equals(ItemStack a, ItemStack b) {
		ItemMeta metaA = a.getItemMeta();
		ItemMeta metaB = b.getItemMeta();

		if (!a.getType().equals(b.getType())) return false;

		// Enchants
		if (!enchantsEqual(a.getEnchantments(), b.getEnchantments())) return false;

		if (metaA == null && metaB == null) return true;
		if (metaA == null || metaB == null) return false;

		// Display name
		if (!metaA.getDisplayName().equals(metaB.getDisplayName())) return false;

		// Coloring
		if (metaA instanceof LeatherArmorMeta) {
			if (!(metaB instanceof LeatherArmorMeta)) return false;
			LeatherArmorMeta colorA = (LeatherArmorMeta) metaA;
			LeatherArmorMeta colorB = (LeatherArmorMeta) metaB;
			if (!colorA.getColor().equals(colorB.getColor())) return false;
		}

		// Lore
		if (metaA.getLore() != null && metaB.getLore() != null) {
			for (String lore : metaA.getLore()) {
				if (!metaB.getLore().contains(lore)) return false;
			}
		} else if (!(metaA.getLore() == null && metaB.getLore() == null)) return false;

		// Books
		if (metaA instanceof BookMeta) {
			if (!(metaB instanceof BookMeta)) return false;

			BookMeta bookA = (BookMeta) metaA;
			BookMeta bookB = (BookMeta) metaB;

			// Author
			if (bookA.getAuthor() != null && !bookA.getAuthor().equals(bookB.getAuthor())) {
				return false;
			}

			if (bookA.getTitle() != null && !bookA.getTitle().equals(bookB.getTitle())) {
				return false;
			}

			// Pages
			if (bookA.getPageCount() != bookB.getPageCount()) return false;

			for (int page = 0; page < bookA.getPages().size(); page++) {
				String pageContentA = bookA.getPages().get(page);
				if (pageContentA != null) {
					if (!pageContentA.equals(bookB.getPages().get(page))) return false;
				}
			}
		}

		// Enchanted books
		if (metaA instanceof EnchantmentStorageMeta) {

			if (!(metaB instanceof EnchantmentStorageMeta)) return false;

			EnchantmentStorageMeta enchA = (EnchantmentStorageMeta) metaA;
			EnchantmentStorageMeta enchB = (EnchantmentStorageMeta) metaB;

			if (enchA.hasStoredEnchants() != enchB.hasStoredEnchants()) return false;

			if (!enchantsEqual(enchA.getStoredEnchants(), enchB.getStoredEnchants())) return false;

		}

		// Skulls
		if (metaA instanceof SkullMeta) {
			if (!(metaB instanceof SkullMeta)) return false;

			SkullMeta skullA = (SkullMeta) metaA;
			SkullMeta skullB = (SkullMeta) metaB;

			if (skullA.getOwningPlayer() != null) {
				if (!skullA.getOwningPlayer().equals(skullB.getOwningPlayer())) return false;
			} else {
				if (skullB.getOwningPlayer() != null) return false;
			}
		}

		// Potions
		if (metaA instanceof PotionMeta) {
			if (!(metaB instanceof PotionMeta)) return false;

			PotionMeta potA = (PotionMeta) metaA;
			PotionMeta potB = (PotionMeta) metaB;

			for (int c = 0; c < potA.getCustomEffects().size(); c++) {
				PotionEffect e = potA.getCustomEffects().get(c);
				if (!e.equals(potB.getCustomEffects().get(c))) return true;
			}
		}

		// Fireworks
		if (metaA instanceof FireworkMeta) {
			if (!(metaB instanceof FireworkMeta)) return false;

			FireworkMeta fwA = (FireworkMeta) metaA;
			FireworkMeta fwB = (FireworkMeta) metaB;

			if (fwA.getPower() != fwB.getPower()) return false;

			for (int e = 0; e < fwA.getEffects().size(); e++) {
				if (!fwA.getEffects().get(e).equals(fwB.getEffects().get(e))) return false;
			}
		}

		// Firework Effects
		if (metaA instanceof FireworkEffectMeta) {
			if (!(metaB instanceof FireworkEffectMeta)) return false;

			FireworkEffectMeta fwA = (FireworkEffectMeta) metaA;
			FireworkEffectMeta fwB = (FireworkEffectMeta) metaB;

			FireworkEffect effectA = fwA.getEffect();
			FireworkEffect effectB = fwB.getEffect();

			if (!effectA.getType().equals(effectB.getType())) return false;

			if (effectA.getColors().size() != effectB.getColors().size()) return false;

			// Colors
			for (int c = 0; c < effectA.getColors().size(); c++) {
				if (!effectA.getColors().get(c).equals(effectB.getColors().get(c))) return false;
			}

			if (effectA.getFadeColors().size() != effectB.getFadeColors().size()) return false;

			// Fade colors
			for (int c = 0; c < effectA.getFadeColors().size(); c++) {
				if (!effectA.getFadeColors().get(c).equals(effectB.getFadeColors().get(c))) return false;
			}

			if (effectA.hasFlicker() != effectB.hasFlicker()) return false;
			if (effectA.hasTrail() != effectB.hasTrail()) return false;

		}

		return true;

	}

	protected static boolean enchantsEqual(Map<Enchantment, Integer> a, Map<Enchantment, Integer> b) {
		// Enchants
		if (a.size() != b.size()) return false;

		// Match enchantments and levels
		for (Map.Entry<Enchantment, Integer> entryA : a.entrySet()) {
			// If enchantment not present
			if (!b.containsKey(entryA.getKey())) return false;

			// If levels don't match
			if (!b.get(entryA.getKey()).equals(entryA.getValue())) return false;
		}

		return true;
	}

	public static boolean isSameType(ItemStack a, ItemStack b) {
		return a.getType().equals(b.getType());
	}

	public static boolean isValidItem(ItemStack item) {
		return (item != null && !item.getType().equals(Material.AIR));
	}

	public static String smallString(ItemStack stack) {
		if (stack != null) {
			String result = stack.getType().name().toLowerCase();

			short durability = (short) getItemDamage(stack);
			if (durability > 0)
				result += ":" + durability;
			return result;
		}
		return null;
	}

	public static void setItemDamage(ItemStack stack, int damage) {
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(stack.getType());

		if(meta instanceof Damageable) {
			Damageable d = (Damageable) meta;

			d.setDamage(damage);
			stack.setItemMeta(meta);
		}
	}

	public static int getItemDamage(ItemStack stack) {
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(stack.getType());

		if(meta instanceof Damageable) {
			Damageable d = (Damageable) meta;

			return d.getDamage();
		}

		return 0;
	}

	public static ItemStack itemOf(String smallString) {
		if (smallString != null) {
			String[] parts = smallString.split(":", 2);
			Material mat = Material.matchMaterial(parts[0].toUpperCase());

			if (mat != null) {
				if (parts.length > 1)
					try {
						ItemStack stack = new ItemStack(mat, 1);
						setItemDamage(stack, Short.valueOf(parts[1]));

						return stack;
					} catch (NumberFormatException ignored) {
					}

				return new ItemStack(mat, 1);
			}
		}
		return null;
	}


	public static String getNiceName(Material material) {
		return getNiceName(new ItemStack(material));
	}

	public static String getNiceName(ItemStack item) {
		StringBuilder item_name = new StringBuilder();

		// Leather Coloring
		if (item.getType().name().contains("LEATHER_")) {
			LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
			// @todo check for undyed somehow
			if (lam.getColor() != null) {
				item_name.append("dyed ");
			}
		}

		// Skull Owner
		else if (MaterialTag.SKULLS.isTagged(item.getType())) {
			SkullMeta skull = (SkullMeta) item.getItemMeta();
			if (skull.hasOwner()) {
				item_name.append(skull.getOwner()).append("'s ");
			}
		}

		if (item_name.length() == 0) {
			item_name.append(item.getType().toString().toLowerCase().replace("_", " "));
		}

		// Written books
		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta meta = (BookMeta) item.getItemMeta();
			if (meta != null) {
				item_name.append(" '").append(meta.getTitle()).append("' by ").append(meta.getAuthor());
			}
		}

		// Enchanted books
		else if (item.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta bookEnchantments = (EnchantmentStorageMeta) item.getItemMeta();
			if (bookEnchantments.hasStoredEnchants()) {
				int i = 1;
				Map<Enchantment, Integer> enchs = bookEnchantments.getStoredEnchants();
				if (enchs.size() > 0) {
					item_name.append(" with");
					for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
						item_name.append(" ").append(EnchantmentUtils.getClientSideEnchantmentName(ench.getKey(), ench.getValue()));
						item_name.append(i < enchs.size() ? ", " : "");
						i++;
					}
				}
			}
		}

		// Enchantments
		int i = 1;
		Map<Enchantment, Integer> enchs = item.getEnchantments();
		if (enchs.size() > 0) {
			item_name.append(" with");
			for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
				item_name.append(" ").append(EnchantmentUtils.getClientSideEnchantmentName(ench.getKey(), ench.getValue()));
				item_name.append(i < enchs.size() ? ", " : "");
				i++;
			}
		}

		// Fireworks
		if (item.getType().equals(Material.FIREWORK_STAR)) {
			FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) item.getItemMeta();
			if (fireworkMeta.hasEffect()) {
				FireworkEffect effect = fireworkMeta.getEffect();
				if (!effect.getColors().isEmpty()) {
					item_name.append(" ").append(effect.getColors().size()).append(" colors");
				}
				if (!effect.getFadeColors().isEmpty()) {
					item_name.append(" ").append(effect.getFadeColors().size()).append(" fade colors");
				}
				if (effect.hasFlicker()) {
					item_name.append(" flickering");
				}
				if (effect.hasTrail()) {
					item_name.append(" with trail");
				}
			}
		}

		// Custom item names
		ItemMeta im = item.getItemMeta();
		if (im != null) {
			item_name.append(", named " + ChatColor.DARK_AQUA + "\"").append(im.getDisplayName()).append(ChatColor.DARK_AQUA).append("\"");
		}

		return item_name.toString();
	}

	public static void dropItem(Location location, ItemStack itemStack) {
		if (location.getWorld() != null && itemStack != null)
			location.getWorld().dropItemNaturally(location, itemStack);
	}

}