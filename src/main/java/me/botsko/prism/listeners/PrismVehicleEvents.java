package me.botsko.prism.listeners;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionFactory;
import me.botsko.prism.actionlibs.InternalActionType;
import me.botsko.prism.actionlibs.RecordingQueue;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class PrismVehicleEvents implements Listener {
	private final Prism plugin;

	public PrismVehicleEvents(Prism plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVehicleCreate(final VehicleCreateEvent event) {

		final Vehicle vehicle = event.getVehicle();
		final Location loc = vehicle.getLocation();

		final String coord_key = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
		final Player player = plugin.preplannedVehiclePlacement.get(coord_key);
		if (player != null) {
			if (!Prism.getIgnore().event(InternalActionType.VEHICLE_PLACE.get(), loc.getWorld(), player))
				return;
			RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_PLACE.get(), vehicle, player.getName()));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVehicleDestroy(final VehicleDestroyEvent event) {

		final Vehicle vehicle = event.getVehicle();
		final Entity attacker = event.getAttacker();
		// Was it broken by an attack
		if (attacker != null) {
			if (attacker instanceof Player) {
				if (!Prism.getIgnore().event(InternalActionType.VEHICLE_BREAK.get(), ((Player) attacker)))
					return;
				RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_BREAK.get(), vehicle,
						attacker.getName()));
			} else {
				if (!Prism.getIgnore().event(InternalActionType.VEHICLE_BREAK.get(), attacker.getWorld()))
					return;
				RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_BREAK.get(), vehicle, attacker.getType().name()
						.toLowerCase()));
			}
		} else {
			// Otherwise its driver was reckless
			final Entity passenger = vehicle.getPassengers().get(0);
			if (passenger instanceof Player) {
				if (!Prism.getIgnore().event(InternalActionType.VEHICLE_BREAK.get(), ((Player) passenger)))
					return;
				RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_BREAK.get(), vehicle,
						passenger.getName()));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVehicleEnter(final VehicleEnterEvent event) {

		final Vehicle vehicle = event.getVehicle();

		final Entity entity = event.getEntered();
		if (entity instanceof Player) {
			if (vehicle instanceof LivingEntity) {
				if (!Prism.getIgnore().event(InternalActionType.ENTITY_ENTER.get(), ((Player) entity)))
					return;
				RecordingQueue.addToQueue(ActionFactory.createEntity(InternalActionType.ENTITY_ENTER.get(), vehicle, entity.getName()));
			} else {
				if (!Prism.getIgnore().event(InternalActionType.VEHICLE_ENTER.get(), ((Player) entity)))
					return;
				RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_ENTER.get(), vehicle, entity.getName()));
			}
		} else {
			if (!Prism.getIgnore().event(InternalActionType.VEHICLE_ENTER.get(), entity.getWorld()))
				return;
			RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_ENTER.get(), vehicle, entity.getType().name()
					.toLowerCase()));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVehicleExit(final VehicleExitEvent event) {

		final Vehicle vehicle = event.getVehicle();

		final Entity entity = event.getExited();
		if (entity instanceof Player) {
			if (vehicle instanceof LivingEntity) {
				if (!Prism.getIgnore().event(InternalActionType.ENTITY_EXIT.get(), ((Player) entity)))
					return;
				RecordingQueue.addToQueue(ActionFactory.createEntity(InternalActionType.ENTITY_EXIT.get(), vehicle, entity.getName()));
			} else {
				if (!Prism.getIgnore().event(InternalActionType.VEHICLE_EXIT.get(), ((Player) entity)))
					return;
				RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_EXIT.get(), vehicle, entity.getName()));
			}
		} else {
			if (!Prism.getIgnore().event(InternalActionType.VEHICLE_EXIT.get(), entity.getWorld()))
				return;
			RecordingQueue.addToQueue(ActionFactory.createVehicle(InternalActionType.VEHICLE_EXIT.get(), vehicle, entity.getType().name()
					.toLowerCase()));
		}
	}

}