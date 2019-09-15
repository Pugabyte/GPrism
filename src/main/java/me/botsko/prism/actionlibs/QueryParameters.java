package me.botsko.prism.actionlibs;

import me.botsko.prism.appliers.PrismProcessType;
import me.botsko.prism.commandlibs.Flag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Query Parameters allows you to add values with which Prism will build the
 * database queries.
 *
 * @author botskonet
 */
@SuppressWarnings("ALL")
public class QueryParameters implements Cloneable {
	protected final ArrayList<String> defaultsUsed = new ArrayList<>();
	protected final ArrayList<Location> specific_block_locations = new ArrayList<>();
	protected final ArrayList<Material> block_filters = new ArrayList<>();
	protected final HashMap<String, MatchRule> entity_filters = new HashMap<>();
	protected final HashMap<String, MatchRule> player_names = new HashMap<>();
	protected final ArrayList<Flag> flags = new ArrayList<>();
	protected final ArrayList<CommandSender> shared_players = new ArrayList<>();
	protected Set<String> foundArgs = new HashSet<>();
	protected PrismProcessType processType = PrismProcessType.LOOKUP;
	protected String original_command;
	protected boolean allow_no_radius = false;
	protected int id = 0;
	protected int minId = 0;
	protected int maxId = 0;
	protected Vector maxLoc;
	protected Vector minLoc;
	protected int parent_id = 0;
	protected Location player_location;
	protected int radius;
	protected Long since_time;
	protected Long before_time;
	protected String world;
	protected String keyword;
	protected boolean ignoreTime;
	protected HashMap<String, MatchRule> actionTypeRules = new HashMap<>();

	protected int per_page = 5;
	protected int limit = 1000000;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMinPrimaryKey() {
		return this.minId;
	}

	public void setMinPrimaryKey(int minId) {
		this.minId = minId;
	}

	public int getMaxPrimaryKey() {
		return this.maxId;
	}

	public void setMaxPrimaryKey(int maxId) {
		this.maxId = maxId;
	}

	public HashMap<String, MatchRule> getEntities() {
		return entity_filters;
	}

	public void addEntity(String entity) {
		addEntity(entity, MatchRule.INCLUDE);
	}

	public void addEntity(String entity, MatchRule match) {
		this.entity_filters.put(entity, match);
	}

	public ArrayList<Material> getBlockFilters() {
		return block_filters;
	}

	public void addBlockFilter(Material material) {
		this.block_filters.add(material);
	}

	public ArrayList<Location> getSpecificBlockLocations() {
		return specific_block_locations;
	}

	public void setSpecificBlockLocation(Location loc) {
		this.specific_block_locations.clear();
		addSpecificBlockLocation(loc);
	}

	public void addSpecificBlockLocation(Location loc) {
		this.specific_block_locations.add(loc);
	}

	public Location getPlayerLocation() {
		return player_location;
	}

	public void setMinMaxVectorsFromPlayerLocation(Location loc) {
		this.player_location = loc;
		if (radius > 0) {
			minLoc = new Vector(loc.getX() - radius, loc.getY() - radius, loc.getZ() - radius);
			maxLoc = new Vector(loc.getX() + radius, loc.getY() + radius, loc.getZ() + radius);
		}
	}

	public void resetMinMaxVectors() {
		minLoc = null;
		maxLoc = null;
	}

	public Vector getMinLocation() {
		return minLoc;
	}

	public void setMinLocation(Vector minLoc) {
		this.minLoc = minLoc;
	}

	public Vector getMaxLocation() {
		return maxLoc;
	}

	public void setMaxLocation(Vector maxLoc) {
		this.maxLoc = maxLoc;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean allowsNoRadius() {
		return allow_no_radius;
	}

	public void setAllowNoRadius(boolean allow_no_radius) {
		this.allow_no_radius = allow_no_radius;
	}

	public HashMap<String, MatchRule> getPlayerNames() {
		return player_names;
	}

	public void addPlayerName(String player) {
		addPlayerName(player, MatchRule.INCLUDE);
	}

	public void addPlayerName(String player, MatchRule match) {
		this.player_names.put(player, match);
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public HashMap<String, MatchRule> getActionTypes() {
		return actionTypeRules;
	}

	public HashMap<String, MatchRule> getActionTypeNames() {
		return actionTypeRules;
	}

	public void addActionType(String action_type) {
		addActionType(action_type, MatchRule.INCLUDE);
	}

	public void addActionType(String action_type, MatchRule match) {
		this.actionTypeRules.put(action_type, match);
	}

	public void removeActionType(ActionType a) {
		actionTypeRules.remove(a.getName());
	}

	public void resetActionTypes() {
		actionTypeRules.clear();
	}

	public Long getBeforeTime() {
		return before_time;
	}

	public void setBeforeTime(Long epoch) {
		this.before_time = epoch;
	}

	public Long getSinceTime() {
		return since_time;
	}

	public void setSinceTime(Long epoch) {
		this.since_time = epoch;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public PrismProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(PrismProcessType lookup_type) {
		this.processType = lookup_type;
	}

	public Set<String> getFoundArgs() {
		return foundArgs;
	}

	public void setFoundArgs(Set<String> foundArgs) {
		this.foundArgs = foundArgs;
	}

	public int getParentId() {
		return parent_id;
	}

	public void setParentId(int id) {
		this.parent_id = id;
	}

	/**
	 * LOOKUP = Most recent actions first. ROLLBACK = Newest->Oldest so we can
	 * "rewind" the events RESTORE = Oldest->Newest so we can "replay" the
	 * events
	 */
	public String getSortDirection() {
		if (!this.processType.equals(PrismProcessType.RESTORE)) {
			return "DESC";
		}
		return "ASC";
	}

	public void addFlag(Flag flag) {
		if (hasFlag(flag))
			return;
		this.flags.add(flag);
	}

	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
	}

	public int getPerPage() {
		return per_page;
	}

	public void setPerPage(int per_page) {
		this.per_page = per_page;
	}

	public void addDefaultUsed(String d) {
		defaultsUsed.add(d);
	}

	public ArrayList<String> getDefaultsUsed() {
		return defaultsUsed;
	}

	public void setStringFromRawArgs(String[] args, int start) {
		StringBuilder params = new StringBuilder();
		if (args.length > 0) {
			for (int i = start; i < args.length; i++) {
				params.append(" ").append(args[i]);
			}
		}
		original_command = params.toString();
	}

	public String getOriginalCommand() {
		return original_command;
	}

	/**
	 * Get the players that you're sharing your lookup with.
	 */
	public ArrayList<CommandSender> getSharedPlayers() {
		return shared_players;
	}

	/**
	 * Set the players you're sharing the lookup with.
	 */
	public void addSharedPlayer(CommandSender sender) {
		this.shared_players.add(sender);
	}

	@Override
	public QueryParameters clone() throws CloneNotSupportedException {
		final QueryParameters cloned = (QueryParameters) super.clone();
		cloned.actionTypeRules = new HashMap<String, MatchRule>(actionTypeRules);
		return cloned;
	}

	/**
	 * Check if we are ignoring the time.
	 */
	public boolean getIgnoreTime() {
		return ignoreTime;
	}

	/**
	 * Ignore the time.
	 */
	public void setIgnoreTime(boolean ignore) {
		this.ignoreTime = ignore;
	}

}
