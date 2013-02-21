package me.botsko.prism.bridge;

import me.botsko.prism.Prism;
import me.botsko.prism.actions.ActionType;
import me.botsko.prism.actions.WorldeditAction;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;

public class PrismBlockEditSession extends EditSession {

	/**
	 * 
	 */
	private LocalPlayer player;

	
	/**
	 * 
	 * @param world
	 * @param maxBlocks
	 * @param player
	 */
	public PrismBlockEditSession(LocalWorld world, int maxBlocks, LocalPlayer player) {
		super(world, maxBlocks);
		this.player = player;
	}

	
	/**
	 * 
	 * @param world
	 * @param maxBlocks
	 * @param blockBag
	 * @param player
	 */
	public PrismBlockEditSession(LocalWorld world, int maxBlocks, BlockBag blockBag, LocalPlayer player) {
		super(world, maxBlocks, blockBag);
		this.player = player;
	}

	
	/**
	 * 
	 */
	@Override
	public boolean rawSetBlock(Vector pt, BaseBlock block) {
		if (!(player.getWorld() instanceof BukkitWorld)) {
			return super.rawSetBlock(pt, block);
		}
		int typeBefore = ((BukkitWorld) player.getWorld()).getWorld().getBlockTypeIdAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
		boolean success = super.rawSetBlock(pt, block);
		if (success) {
			Location loc = new Location(Bukkit.getWorld(player.getWorld().getName()), pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
			Prism.actionsRecorder.addToQueue( new WorldeditAction(ActionType.WORLD_EDIT, loc, typeBefore, 0, loc.getBlock().getTypeId(), loc.getBlock().getData(), player.getName()) );
		}
		return success;
	}
}