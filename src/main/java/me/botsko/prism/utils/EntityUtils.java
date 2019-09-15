package me.botsko.prism.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EntityUtils {

	public static OfflinePlayer offlineOf(String uuidOrName) {
		if (uuidOrName != null) {
			OfflinePlayer result;
			try {
				result = Bukkit.getOfflinePlayer(UUID.fromString(uuidOrName));
			}
			catch (IllegalArgumentException e) {
				@SuppressWarnings("deprecation")
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuidOrName);
				result = player;
			}

			return result.hasPlayedBefore() ? result : null;
		}

		return null;
	}


	public static int removeNearbyItemDrops(Player player, int radius) {
		int removed = 0;
		List<Entity> nearby = player.getNearbyEntities(radius, radius, radius);
		for (Entity e : nearby) {
			if (e instanceof Item || e instanceof ExperienceOrb) {
				e.remove();
				removed++;
			}
		}
		return removed;
	}

	public static boolean inCube(Location loc1, int radius, Location loc2) {
		if (loc1 == null || loc2 == null) return false;
		return
				loc1.getBlockX() + radius > loc2.getBlockX()
						&& loc1.getBlockX() - radius < loc2.getBlockX()
						&& loc1.getBlockY() + radius > loc2.getBlockY()
						&& loc1.getBlockY() - radius < loc2.getBlockY()
						&& loc1.getBlockZ() + radius > loc2.getBlockZ()
						&& loc1.getBlockZ() - radius < loc2.getBlockZ()
				;
	}

	public static EntityType getEntityType(String name) {
		try {
			return EntityType.valueOf(name.toUpperCase());
		} catch (Exception ignored) {}

		return null;
	}

	private static HashMap<String, String> descriptionCache = new HashMap<>();

	public static String getCustomProjectileDescription(Projectile source) {
		String description = descriptionCache.get(source.getClass().getSimpleName());

		if (description == null) {
			if (source instanceof Trident) {
				description = "scewered";
			}
			else if (source instanceof Arrow) {
				description = "shot";
			}
			else if (source instanceof Egg) {
				description = "became the very best of";
			}
			else if (source instanceof EnderPearl) {
				description = "vwooped";
			}
			// Before generic Fireball
			else if (source instanceof SmallFireball) {
				description = "ignited";
			}
			else if (source instanceof Fireball) {
				description = "exploded";
			}
			else if (source instanceof FishHook) {
				description = "hooked";
			}
			else if (source instanceof ThrownPotion) {
				description = "doused";
			}
			else if (source instanceof LlamaSpit) {
				description = "disrespected";
			}
			else if (source instanceof ShulkerBullet) {
				description = "ascended";
			}
			else if (source instanceof Snowball) {
				description = "iced";
			}
			else if (source instanceof ThrownExpBottle) {
				description = "taught";
			}
			else {
				description = "";
			}

			descriptionCache.put(source.getClass().getSimpleName(), description);
		}

		if (description.length() > 0) {
			return description;
		}

		return null;
	}

}