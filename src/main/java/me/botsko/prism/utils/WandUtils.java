package me.botsko.prism.utils;

import me.botsko.prism.Prism;
import me.botsko.prism.wands.Wand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WandUtils {

	public static boolean playerUsesWandOnClick(Player player, Location loc) {
		if (Prism.playersWithActiveTools.containsKey(player.getName())) {

			final Wand wand = Prism.playersWithActiveTools.get(player.getName());

			if (wand == null)
				return false;

			if (player.getInventory().getItemInMainHand().getType().equals(wand.getItemType())) {
				wand.playerLeftClick(player, loc);
				return true;
			}
		}

		return false;
	}

}