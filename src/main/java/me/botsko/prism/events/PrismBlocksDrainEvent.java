package me.botsko.prism.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class PrismBlocksDrainEvent extends Event {

	/**
	 * Required by bukkit for proper event handling.
	 */
	private static final HandlerList handlers = new HandlerList();
	/**
	 *
	 */
	protected final int radius;
	/**
	 *
	 */
	private final ArrayList<BlockStateChange> blockStateChanges;
	/**
	 *
	 */
	private final Player onBehalfOf;

	/**
	 * @param blockStateChanges
	 * @param onBehalfOf
	 * @param radius
	 */
	public PrismBlocksDrainEvent(ArrayList<BlockStateChange> blockStateChanges, Player onBehalfOf, int radius) {
		this.blockStateChanges = blockStateChanges;
		this.onBehalfOf = onBehalfOf;
		this.radius = radius;
	}

	/**
	 * Required by bukkit for proper event handling.
	 *
	 * @return
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * @return the originalBlock
	 */
	public ArrayList<BlockStateChange> getBlockStateChanges() {
		return blockStateChanges;
	}

	/**
	 * @return the onBehalfOf
	 */
	public Player onBehalfOf() {
		return onBehalfOf;
	}

	/**
	 * @return
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * Required by bukkit for proper event handling.
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
