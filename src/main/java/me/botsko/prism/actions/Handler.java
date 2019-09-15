package me.botsko.prism.actions;

import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.appliers.ChangeResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public interface Handler {

	void setPlugin(Plugin pl);

	int getId();

	void setId(int id);

	String getUnixEpoch();

	void setUnixEpoch(String epoch);

	String getDisplayDate();

	String getDisplayTime();

	String getTimeSince();

	ActionType getActionType();

	void setActionType(ActionType actionType);

	String getSourceName();

	void setSourceName(String name);

	OfflinePlayer getPlayer();

	void setPlayer(OfflinePlayer player);

	void setUuid(UUID uuid);

	UUID getUuid();

	Location getLocation();

	World getWorld();

	void setWorld(World world);

	double getX();

	void setX(double x);

	double getY();

	void setY(double y);

	double getZ();

	void setZ(double z);

	Material getMaterial();

	void setMaterial(Material material);

	BlockData getBlockData();

	void setBlockData(BlockData blockData);

	short getDurability();

	void setDurability(short durability);

	Material getOldMaterial();

	void setOldMaterial(Material oldMaterial);

	BlockData getOldBlockData();

	void setOldBlockData(BlockData oldBlockData);

	short getOldDurability();

	void setOldDurability(short oldDurability);

	boolean hasExtraData();

	String serialize();

	void deserialize(String data);

	int getWasRollback();

	void setWasRollback(int rollback);

	int getAggregateCount();

	void setAggregateCount(int aggregateCount);

	String getNiceName();

	String getCustomDescription();

	void setCustomDescription(String description);

	void save();

	boolean isCanceled();

	void setCanceled(boolean cancel);

	ChangeResult applyRollback(Player player, QueryParameters parameters, boolean is_preview);

	ChangeResult applyRestore(Player player, QueryParameters parameters, boolean is_preview);

	ChangeResult applyUndo(Player player, QueryParameters parameters, boolean is_preview);

	ChangeResult applyDeferred(Player player, QueryParameters parameters, boolean is_preview);

}