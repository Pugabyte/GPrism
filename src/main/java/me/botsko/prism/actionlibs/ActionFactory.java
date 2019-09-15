package me.botsko.prism.actionlibs;

import java.util.Locale;
import java.util.Map;

import me.botsko.prism.actions.BlockAction;
import me.botsko.prism.actions.BlockChangeAction;
import me.botsko.prism.actions.BlockShiftAction;
import me.botsko.prism.actions.EntityAction;
import me.botsko.prism.actions.EntityTravelAction;
import me.botsko.prism.actions.GrowAction;
import me.botsko.prism.actions.Handler;
import me.botsko.prism.actions.HangingItemAction;
import me.botsko.prism.actions.ItemStackAction;
import me.botsko.prism.actions.PlayerAction;
import me.botsko.prism.actions.PlayerDeathAction;
import me.botsko.prism.actions.PrismProcessAction;
import me.botsko.prism.actions.PrismRollbackAction;
import me.botsko.prism.actions.SignAction;
import me.botsko.prism.actions.UseAction;
import me.botsko.prism.actions.VehicleAction;
import me.botsko.prism.appliers.PrismProcessType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ActionFactory {

	public static Handler createBlock(InternalActionType internalActionType, OfflinePlayer player) {
		return createBlock(internalActionType.get(), player);
	}

	public static Handler createBlock(ActionType actionType, OfflinePlayer player) {
		final BlockAction a = new BlockAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		return a;
	}

	public static Handler createBlock(InternalActionType internalActionType, Block block, OfflinePlayer player) {
		return createBlock(internalActionType.get(), block, player);
	}

	public static Handler createBlock(ActionType actionType, Block block, OfflinePlayer player) {
		final BlockAction a = new BlockAction();
		a.setActionType(actionType);
		a.setBlock(block);
		a.setPlayer(player);
		return a;
	}

	public static Handler createBlock(InternalActionType internalActionType, Block block, String nonPlayer) {
		return createBlock(internalActionType.get(), block, nonPlayer);
	}

	public static Handler createBlock(ActionType actionType, Block block, String nonPlayer) {
		final Handler a = createBlock(actionType, block, (OfflinePlayer) null);
		a.setSourceName(nonPlayer);
		return a;
	}

	public static Handler createBlock(InternalActionType internalActionType, BlockState state, OfflinePlayer player) {
		return createBlock(internalActionType.get(), state, player);
	}

	public static Handler createBlock(ActionType actionType, BlockState state, OfflinePlayer player) {
		final BlockAction a = new BlockAction();
		a.setActionType(actionType);
		a.setBlock(state);
		a.setPlayer(player);
		return a;
	}

	public static Handler createBlock(InternalActionType internalActionType, BlockState block, String nonPlayer) {
		return createBlock(internalActionType.get(), block, nonPlayer);
	}

	public static Handler createBlock(ActionType actionType, BlockState block, String nonPlayer) {
		final Handler a = createBlock(actionType, block, (OfflinePlayer) null);
		a.setSourceName(nonPlayer);
		return a;
	}

	public static Handler createBlockChange(InternalActionType internalActionType, Location loc, Material oldMat, BlockData oldData, Material newMat, BlockData newData, OfflinePlayer player) {
		return createBlockChange(internalActionType.get(), loc, oldMat, oldData, newMat, newData, player);
	}

	public static Handler createBlockChange(ActionType actionType, Location loc, Material oldMat, BlockData oldData, Material newMat, BlockData newData, OfflinePlayer player) {
		final BlockChangeAction a = new BlockChangeAction();
		a.setActionType(actionType);
		a.setMaterial(newMat);
		a.setBlockData(newData);
		a.setOldMaterial(oldMat);
		a.setOldBlockData(oldData);
		a.setPlayer(player);
		a.setLocation(loc);
		return a;
	}

	public static Handler createBlockChange(InternalActionType internalActionType, Location loc, Material oldMat, BlockData oldData, Material newMat, BlockData newData, String nonPlayer) {
		return createBlockChange(internalActionType.get(), loc, oldMat, oldData, newMat, newData, nonPlayer);
	}

	public static Handler createBlockChange(ActionType actionType, Location loc, Material oldMat, BlockData oldData, Material newMat, BlockData newData, String nonPlayer) {
		final Handler a = createBlockChange(actionType, loc, oldMat, oldData, newMat, newData, (OfflinePlayer) null);
		a.setSourceName(nonPlayer);
		return a;
	}

	public static Handler createBlockShift(InternalActionType internalActionType, Block from, Location to, String nonPlayer) {
		return createBlockShift(internalActionType.get(), from, to, nonPlayer);
	}

	public static Handler createBlockShift(ActionType actionType, Block from, Location to, String nonPlayer) {
		final BlockShiftAction a = new BlockShiftAction();
		a.setActionType(actionType);
		a.setBlock(from);
		a.setSourceName(nonPlayer);
		a.setLocation(to);
		return a;
	}

	public static Handler createEntity(InternalActionType internalActionType, Entity entity, OfflinePlayer player) {
		return createEntity(internalActionType.get(), entity, player);
	}

	public static Handler createEntity(ActionType actionType, Entity entity, OfflinePlayer player) {
		return ActionFactory.createEntity(actionType, entity, player, null);
	}

	public static Handler createEntity(InternalActionType internalActionType, Entity entity, String nonPlayer) {
		return createEntity(internalActionType.get(), entity, nonPlayer);
	}

	public static Handler createEntity(ActionType actionType, Entity entity, String nonPlayer) {
		return ActionFactory.createEntity(actionType, entity, nonPlayer, null);
	}

	public static Handler createEntity(InternalActionType internalActionType, Entity entity, OfflinePlayer player, String dyeUsed) {
		return createEntity(internalActionType.get(), entity, player, dyeUsed);
	}

	public static Handler createEntity(ActionType actionType, Entity entity, OfflinePlayer player, String dyeUsed) {
		final EntityAction a = new EntityAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setEntity(entity, dyeUsed);
		return a;
	}

	public static Handler createEntity(InternalActionType internalActionType, Entity entity, String nonPlayer, String dyeUsed) {
		return createEntity(internalActionType.get(), entity, nonPlayer, dyeUsed);
	}

	public static Handler createEntity(ActionType actionType, Entity entity, String nonPlayer, String dyeUsed) {
		final Handler a = createEntity(actionType, entity, (OfflinePlayer) null, dyeUsed);
		a.setSourceName(nonPlayer);
		return a;
	}

	public static Handler createEntityTravel(InternalActionType internalActionType, Entity entity, Location from, Location to, TeleportCause cause) {
		return createEntityTravel(internalActionType.get(), entity, from, to, cause);
	}

	public static Handler createEntityTravel(ActionType actionType, Entity entity, Location from, Location to, TeleportCause cause) {
		final EntityTravelAction a = new EntityTravelAction();
		a.setEntity(entity);
		a.setActionType(actionType);
		a.setLocation(from);
		a.setToLocation(to);
		a.setCause(cause);
		return a;
	}

	public static Handler createGrow(InternalActionType internalActionType, BlockState blockstate, OfflinePlayer player) {
		return createGrow(internalActionType.get(), blockstate, player);
	}

	public static Handler createGrow(ActionType actionType, BlockState blockstate, OfflinePlayer player) {
		final GrowAction a = new GrowAction();
		a.setActionType(actionType);
		a.setBlock(blockstate);
		a.setPlayer(player);
		return a;
	}

	public static Handler createGrow(InternalActionType internalActionType, BlockState blockstate, String nonPlayer) {
		return createGrow(internalActionType.get(), blockstate, nonPlayer);
	}

	public static Handler createGrow(ActionType actionType, BlockState blockstate, String nonPlayer) {
		final Handler a = createGrow(actionType, blockstate, (OfflinePlayer) null);
		a.setSourceName(nonPlayer);
		return a;
	}

	public static Handler createHangingItem(InternalActionType internalActionType, Hanging hanging, OfflinePlayer player) {
		return createHangingItem(internalActionType.get(), hanging, player);
	}

	public static Handler createHangingItem(ActionType actionType, Hanging hanging, OfflinePlayer player) {
		final HangingItemAction a = new HangingItemAction();
		a.setActionType(actionType);
		a.setHanging(hanging);
		a.setPlayer(player);
		return a;
	}

	public static Handler createHangingItem(InternalActionType internalActionType, Hanging hanging, String nonPlayer) {
		return createHangingItem(internalActionType.get(), hanging, nonPlayer);
	}

	public static Handler createHangingItem(ActionType actionType, Hanging hanging, String nonPlayer) {
		final Handler a = createHangingItem(actionType, hanging, (OfflinePlayer) null);
		a.setSourceName(nonPlayer);
		return a;
	}

	public static Handler createItemStack(InternalActionType internalActionType, ItemStack item, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		return createItemStack(internalActionType.get(), item, enchantments, loc, player);
	}

	public static Handler createItemStack(ActionType actionType, ItemStack item, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		return ActionFactory.createItemStack(actionType, item, 1, -1, enchantments, loc, player);
	}

	public static Handler createItemStack(InternalActionType internalActionType, ItemStack item, int quantity, int slot, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		return createItemStack(internalActionType.get(), item, quantity, slot, enchantments, loc, player);
	}

	public static Handler createItemStack(ActionType actionType, ItemStack item, int quantity, int slot, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		final ItemStackAction a = createItemStack(actionType, item, quantity, enchantments, loc, player);
		a.setSlot(String.valueOf(slot));
		return a;
	}

	public static Handler createItemStack(InternalActionType internalActionType, ItemStack item, int quantity, BlockFace slot, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		return createItemStack(internalActionType.get(), item, quantity, slot, enchantments, loc, player);
	}

	public static Handler createItemStack(ActionType actionType, ItemStack item, int quantity, BlockFace slot, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		final ItemStackAction a = createItemStack(actionType, item, quantity, enchantments, loc, player);
		a.setSlot(slot.name().toLowerCase(Locale.ENGLISH));
		return a;
	}

	public static Handler createItemStack(InternalActionType internalActionType, ItemStack item, int quantity, int slot, Map<Enchantment, Integer> enchantments, Location loc, String sourceName) {
		return createItemStack(internalActionType.get(), item, quantity, slot, enchantments, loc, sourceName);
	}

	public static Handler createItemStack(ActionType actionType, ItemStack item, int quantity, int slot, Map<Enchantment, Integer> enchantments, Location loc, String sourceName) {
		final ItemStackAction a = new ItemStackAction();
		a.setActionType(actionType);
		a.setLocation(loc);
		a.setSourceName(sourceName);
		a.setItem(item, quantity, enchantments);
		a.setSlot(String.valueOf(slot));
		return a;
	}

	public static Handler createItemStack(InternalActionType internalActionType, ItemStack item, int quantity, EquipmentSlot slot, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		return createItemStack(internalActionType.get(), item, quantity, slot, enchantments, loc, player);
	}

	public static Handler createItemStack(ActionType actionType, ItemStack item, int quantity, EquipmentSlot slot, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		final ItemStackAction a = createItemStack(actionType, item, quantity, enchantments, loc, player);
		a.setSlot(slot.name().toLowerCase(Locale.ENGLISH));
		return a;
	}

	private static ItemStackAction createItemStack(ActionType actionType, ItemStack item, int quantity, Map<Enchantment, Integer> enchantments, Location loc, OfflinePlayer player) {
		final ItemStackAction a = new ItemStackAction();
		a.setActionType(actionType);
		a.setLocation(loc);
		a.setPlayer(player);
		a.setItem(item, quantity, enchantments);
		return a;
	}

	public static Handler createPlayer(InternalActionType internalActionType, Player player, String additionalInfo) {
		return createPlayer(internalActionType.get(), player, additionalInfo);
	}

	public static Handler createPlayer(ActionType actionType, Player player, String additionalInfo) {
		final PlayerAction a = new PlayerAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setLocation(player.getLocation());
		a.deserialize(additionalInfo);
		return a;
	}

	public static Handler createPlayerDeath(InternalActionType internalActionType, Player player, String cause, String attacker) {
		return createPlayerDeath(internalActionType.get(), player, cause, attacker);
	}

	public static Handler createPlayerDeath(ActionType actionType, Player player, String cause, String attacker) {
		final PlayerDeathAction a = new PlayerDeathAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setLocation(player.getLocation());
		a.setCause(cause);
		a.setAttacker(attacker);
		return a;
	}

	public static Handler createPrismProcess(InternalActionType internalActionType, PrismProcessType processType, Player player, String parameters) {
		return createPrismProcess(internalActionType.get(), processType, player, parameters);
	}

	public static Handler createPrismProcess(ActionType actionType, PrismProcessType processType, Player player, String parameters) {
		final PrismProcessAction a = new PrismProcessAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setLocation(player.getLocation());
		a.setProcessData(processType, parameters);
		return a;
	}

	public static Handler createPrismRollback(InternalActionType internalActionType, BlockState oldblock, BlockState newBlock, OfflinePlayer player, long parent_id) {
		return createPrismRollback(internalActionType.get(), oldblock, newBlock, player, parent_id);
	}

	public static Handler createPrismRollback(ActionType actionType, BlockState oldblock, BlockState newBlock, OfflinePlayer player, long parent_id) {
		final PrismRollbackAction a = new PrismRollbackAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setLocation(oldblock.getLocation());
		a.setBlockChange(oldblock, newBlock, parent_id);
		return a;
	}

	public static Handler createSign(InternalActionType internalActionType, Block block, String[] lines, OfflinePlayer player) {
		return createSign(internalActionType.get(), block, lines, player);
	}

	public static Handler createSign(ActionType actionType, Block block, String[] lines, OfflinePlayer player) {
		final SignAction a = new SignAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setBlock(block, lines);
		return a;
	}

	public static Handler createUse(InternalActionType internalActionType, Material item, Block block, OfflinePlayer player) {
		return createUse(internalActionType.get(), item, block, player);
	}

	public static Handler createUse(ActionType actionType, Material item, Block block, OfflinePlayer player) {
		final UseAction a = new UseAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setLocation(block.getLocation());
		a.setMaterial(item);
		return a;
	}

	public static Handler createVehicle(InternalActionType internalActionType, Vehicle vehicle, OfflinePlayer player) {
		return createVehicle(internalActionType.get(), vehicle, player);
	}

	public static Handler createVehicle(ActionType actionType, Vehicle vehicle, OfflinePlayer player) {
		final VehicleAction a = new VehicleAction();
		a.setActionType(actionType);
		a.setPlayer(player);
		a.setLocation(vehicle.getLocation());
		a.setVehicle(vehicle);
		return a;
	}

	public static Handler createVehicle(InternalActionType internalActionType, Vehicle vehicle, String nonPlayer) {
		return createVehicle(internalActionType.get(), vehicle, nonPlayer);
	}

	public static Handler createVehicle(ActionType actionType, Vehicle vehicle, String nonPlayer) {
		final Handler a = createVehicle(actionType, vehicle, (OfflinePlayer) null);
		a.setSourceName(nonPlayer);
		return a;
	}

}