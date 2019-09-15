package me.botsko.prism.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.appliers.ChangeResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GenericAction implements Handler {

	protected final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	protected Plugin plugin;
	protected boolean canceled = false;
	protected ActionType actionType;
	protected int id;
	protected String epoch;
	protected String display_date;
	protected String display_time;
	private String sourceName;
	protected UUID uuid;
	protected World world;
	protected double x;
	protected double y;
	protected double z;
	protected Material material;
	protected BlockData blockData;
	protected Material oldMaterial;
	protected BlockData oldBlockData;
	protected int rollback;
	protected int aggregateCount = 0;

	@Override
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getUnixEpoch() {
		return epoch;
	}

	@Override
	public void setUnixEpoch(String epoch) {

		this.epoch = epoch;

		final Date action_time = new Date(Long.parseLong(epoch) * 1000);

		final SimpleDateFormat date = new SimpleDateFormat("yy/MM/dd");
		this.display_date = date.format(action_time);

		final SimpleDateFormat time = new SimpleDateFormat("hh:mm:ssa");
		this.display_time = time.format(action_time);

	}

	@Override
	public String getDisplayDate() {
		return display_date;
	}

	@Override
	public String getDisplayTime() {
		return display_time;
	}

	@Override
	public String getTimeSince() {
		String time_ago = "";

		final Date start = new Date(Long.parseLong(this.epoch) * 1000);
		final Date end = new Date();

		long diffInSeconds = (end.getTime() - start.getTime()) / 1000;

		final long[] diff = new long[]{0, 0, 0, 0};
		/* sec */
		diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		/* min */
		diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		/* hours */
		diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		/* days */
		diff[0] = (diffInSeconds = (diffInSeconds / 24));

		// Only show days if more than 1
		if (diff[0] >= 1) {
			time_ago += diff[0] + "d";
		}
		// Only show hours if > 1
		if (diff[1] >= 1) {
			time_ago += diff[1] + "h";
		}
		// Only show minutes if > 1 and less than 60
		if (diff[2] > 1 && diff[2] < 60) {
			time_ago += diff[2] + "m";
		}
		if (!time_ago.isEmpty()) {
			time_ago += " ago";
		}

		if (diff[0] == 0 && diff[1] == 0 && diff[2] <= 1) {
			time_ago = "just now";
		}

		return time_ago;
	}

	@Override
	public ActionType getActionType() {
		return actionType;
	}

	@Override
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	@Override
	public String getSourceName() {
		return null;
	}

	@Override
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	@Override
	public void setPlayer(OfflinePlayer player) {
		if (player != null) {
			setUuid(player.getUniqueId());
		}
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public Location getLocation() {
		return new Location(getWorld(), getX(), getY(), getZ());
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public double getZ() {
		return z;
	}

	@Override
	public void setZ(double z) {
		this.z = z;
	}

	public void setLocation(Location loc) {
		if (loc != null) {
			this.world = loc.getWorld();
			this.x = loc.getBlockX();
			this.y = loc.getBlockY();
			this.z = loc.getBlockZ();
		}
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public BlockData getBlockData() {
		return blockData;
	}

	public void setBlockData(BlockData blockData) {
		this.blockData = blockData;
	}

	@Override
	public short getDurability() {
		return 0;
	}

	@Override
	public void setDurability(short durability) {

	}

	public Material getOldMaterial() {
		return oldMaterial;
	}

	public void setOldMaterial(Material oldMaterial) {
		this.oldMaterial = oldMaterial;
	}

	public BlockData getOldBlockData() {
		return oldBlockData;
	}

	public void setOldBlockData(BlockData oldBlockData) {
		this.oldBlockData = oldBlockData;
	}

	@Override
	public short getOldDurability() {
		return 0;
	}

	@Override
	public void setOldDurability(short oldDurability) {

	}

	@Override
	public boolean hasExtraData() {
		return false;
	}

	@Override
	public String serialize() {
		return null;
	}

	@Override
	public void deserialize(String data) {

	}

	@Override
	public int getWasRollback() {
		return rollback;
	}

	@Override
	public void setWasRollback(int rollback) {
		this.rollback = rollback;
	}

	@Override
	public int getAggregateCount() {
		return aggregateCount;
	}

	@Override
	public void setAggregateCount(int aggregateCount) {
		this.aggregateCount = aggregateCount;
	}

	@Override
	public String getNiceName() {
		return "something";
	}

	@Override
	public String getCustomDescription() {
		return null;
	}

	@Override
	public void setCustomDescription(String description) {
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean cancel) {
		this.canceled = cancel;
	}

	@Override
	public void save() {
		// data is already set - anything not encoding a json
		// object is already ready.
	}

	@Override
	public ChangeResult applyRollback(Player player, QueryParameters parameters, boolean is_preview) {
		return null;
	}

	@Override
	public ChangeResult applyRestore(Player player, QueryParameters parameters, boolean is_preview) {
		return null;
	}

	@Override
	public ChangeResult applyUndo(Player player, QueryParameters parameters, boolean is_preview) {
		return null;
	}

	@Override
	public ChangeResult applyDeferred(Player player, QueryParameters parameters, boolean is_preview) {
		return null;
	}

}
