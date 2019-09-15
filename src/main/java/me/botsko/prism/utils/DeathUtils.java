package me.botsko.prism.utils;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

public class DeathUtils {

	public static String getCauseNiceName(Entity entity) {

		EntityDamageEvent e = entity.getLastDamageCause();

		if (e == null) {
			return "unknown";
		}

		// Determine the root cause
		DamageCause damageCause = e.getCause();
		Entity killer = null;

		// If was damaged by an entity
		if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entity.getLastDamageCause();
			// Arrow?
			if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();
				ProjectileSource source = arrow.getShooter();
				if (source instanceof Player) {
					killer = ((Player) source);
				}
			} else {
				killer = entityDamageByEntityEvent.getDamager();
			}
		}

		if (entity instanceof Player) {

			Player player = (Player) entity;

			// Detect additional suicide. For example, when you potion
			// yourself with instant damage it doesn't show as suicide.
			if (killer instanceof Player) {
				// Themself
				if (killer.getName().equals(player.getName())) {
					return "suicide";
				}
				// translate bukkit events to nicer names
				if ((damageCause.equals(DamageCause.ENTITY_ATTACK) || damageCause.equals(DamageCause.PROJECTILE))) {
					return "pvp";
				}
			}
		}

		// Causes of death for either entities or players
		switch (damageCause) {
			case ENTITY_ATTACK:
				return "mob";
			case PROJECTILE:
				return "skeleton";
			case ENTITY_EXPLOSION:
				return "creeper";
			case CONTACT:
				return "cactus";
			case BLOCK_EXPLOSION:
				return "tnt";
			case FIRE:
			case FIRE_TICK:
				return "fire";
			case MAGIC:
				return "potion";
		}
		return damageCause.name().toLowerCase();
	}

	/**
	 * Returns the name of the attacker, whether mob or player.
	 */
	public static String getAttackerName(Entity victim) {
		if (victim instanceof Player) {
			Player killer = ((Player) victim).getKiller();
			if (killer != null) {
				return killer.getName();
			}
		}

		String cause = getCauseNiceName(victim);

		if ("mob".equals(cause) && victim.getLastDamageCause() != null) {
			Entity killer = ((EntityDamageByEntityEvent) victim.getLastDamageCause()).getDamager();

			if (killer instanceof Player) {
				return killer.getName();
			}

			if (killer instanceof Projectile) {
				killer = (LivingEntity) ((Projectile) killer).getShooter();
			}

			if (killer != null && killer.getType().getEntityClass() != null) {
				return killer.getType().getEntityClass().getName().toLowerCase();
			}
		}

		return cause;
	}

}