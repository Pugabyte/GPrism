package me.botsko.prism.actionlibs;

import me.botsko.prism.actions.*;

public enum InternalActionType {
	BLOCK_BREAK(false, true, true, BlockAction.class, "broke"),
	BLOCK_BURN(false, true, true, BlockAction.class, "burned"),
	BLOCK_DISPENSE(false, false, false, ItemStackAction.class, "dispensed"),
	BLOCK_FADE(false, true, true, BlockChangeAction.class, "faded"),
	BLOCK_FALL(false, true, true, BlockAction.class, "fell"),
	BLOCK_FORM(false, true, true, BlockChangeAction.class, "formed"),
	BLOCK_PLACE(true, true, true, BlockAction.class, "placed"),
	BLOCK_SHIFT(true, false, false, BlockShiftAction.class, "moved"),
	BLOCK_SPREAD(true, true, true, BlockChangeAction.class, "grew"),
	BLOCK_USE(false, false, false, BlockAction.class, "used"),
	BONEMEAL_USE(false, false, false, UseAction.class, "used"),
	BUCKET_FILL(false, false, false, PlayerAction.class, "filled"),
	 CAKE_EAT(false, false, false, UseAction.class, "ate"),
	CONTAINER_ACCESS(false, false, false, BlockAction.class, "accessed"),
	CRAFT_ITEM(false, false, false, ItemStackAction.class, "crafted"),
	CREEPER_EXPLODE(false, true, true, BlockAction.class, "blew up"),
	CROP_TRAMPLE(false, true, true, BlockAction.class, "trampled"),
	DRAGON_EAT(false, true, true, BlockAction.class, "ate"),
	ENCHANT_ITEM(false, false, false, ItemStackAction.class, "enchanted"),
	ENDERMAN_PICKUP(false, true, true, BlockAction.class, "picked up"),
	ENDERMAN_PLACE(true, true, true, BlockAction.class, "placed"),
	ENTITY_BREAK(true, true, true, BlockAction.class, "broke"),
	ENTITY_DYE(false, false, false, EntityAction.class, "dyed"),
	ENTITY_ENTER(false, false, false, EntityAction.class, "entered"),
	ENTITY_EXIT(false, false, false, EntityAction.class, "exited"),
	ENTITY_EXPLODE(false, true, true, BlockAction.class, "blew up"),
	ENTITY_FOLLOW(false, false, false, EntityAction.class, "lured"),
	ENTITY_FORM(true, true, true, BlockChangeAction.class, "formed"),
	ENTITY_KILL(false, true, false, EntityAction.class, "killed"),
	ENTITY_LEASH(true, false, false, EntityAction.class, "leashed"),
	ENTITY_SHEAR(false, false, false, EntityAction.class, "sheared"),
	ENTITY_SPAWN(false, false, false, EntityAction.class, "spawned"),
	ENTITY_UNLEASH(false, false, false, EntityAction.class, "unleashed"),
	FIRE_SPREAD(true, true, true, BlockChangeAction.class, "spread"),
	FIREBALL(false, false, false, null, "ignited"),
	FIREWORK_LAUNCH(false, false, false, ItemStackAction.class, "launched"),
	HANGINGITEM_BREAK(false, true, true, HangingItemAction.class, "broke"),
	HANGINGITEM_PLACE(true, true, true, HangingItemAction.class, "hung"),
	ITEM_DROP(false, true, true, ItemStackAction.class, "dropped"),
	ITEM_INSERT(false, true, true, ItemStackAction.class, "inserted"),
	ITEM_PICKUP(false, true, true, ItemStackAction.class, "picked up"),
	ITEM_REMOVE(false, true, true, ItemStackAction.class, "removed"),
	ITEM_ROTATE(false, true, true, HangingItemAction.class, "turned item"),
	LAVA_BREAK(false, true, true, BlockAction.class, "broke"),
	LAVA_BUCKET(true, true, true, BlockChangeAction.class, "poured"),
	LAVA_FLOW(true, true, true, BlockAction.class, "flowed into"),
	LAVA_IGNITE(false, false, false, null, "ignited"),
	LEAF_DECAY(false, true, true, BlockAction.class, "decayed"),
	LIGHTER(false, false, false, null, "set a fire"),
	LIGHTNING(false, false, false, null, "ignited"),
	MUSHROOM_GROW(true, true, true, GrowAction.class, "grew"),
	PLAYER_CHAT(false, false, false, PlayerAction.class, "said"),
	PLAYER_COMMAND(false, false, false, PlayerAction.class, "ran command"),
	PLAYER_DEATH(false, false, false, PlayerDeathAction.class, "died"),
	PLAYER_HIT(false, false, false, PlayerAction.class, "hit"),
	PLAYER_JOIN(false, false, false, PlayerAction.class, "joined"),
	PLAYER_KILL(false, true, false, EntityAction.class, "killed"),
	PLAYER_QUIT(false, false, false, PlayerAction.class, "quit"),
	PLAYER_TELEPORT(false, false, false, EntityTravelAction.class, "teleported"),
	POTION_SPLASH(false, false, false, PlayerAction.class, "threw potion"),
	PRISM_DRAIN(false, true, true, PrismRollbackAction.class, "drained"),
	PRISM_EXTINGUISH(false, true, true, PrismRollbackAction.class, "extinguished"),
	PRISM_PROCESS(false, false, false, PrismProcessAction.class, "ran process"),
	PRISM_ROLLBACK(true, false, false, PrismRollbackAction.class, "rolled back"),
	SHEEP_EAT(false, false, false, BlockAction.class, "ate"),
	SIGN_CHANGE(false, false, true, SignAction.class, "wrote"),
	SPAWNEGG_USE(false, false, false, UseAction.class, "used"),
	TNT_EXPLODE(false, true, true, BlockAction.class, "blew up"),
	TNT_PRIME(false, false, false, UseAction.class, "primed"),
	TREE_GROW(true, true, true, GrowAction.class, "grew"),
	VEHICLE_BREAK(false, true, false, VehicleAction.class, "broke"),
	VEHICLE_ENTER(false, false, false, VehicleAction.class, "entered"),
	VEHICLE_EXIT(false, false, false, VehicleAction.class, "exited"),
	VEHICLE_PLACE(true, false, false, VehicleAction.class, "placed"),
	WATER_BREAK(false, true, true, BlockAction.class, "broke"),
	WATER_BUCKET(true, true, true, BlockChangeAction.class, "poured"),
	WATER_FLOW(true, true, true, BlockAction.class, "flowed into"),
	WORLD_EDIT(true, true, true, BlockChangeAction.class, "edited"),
	XP_PICKUP(false, false, false, PlayerAction.class, "picked up");

	private final boolean doesCreateBlock;
	private final boolean canRollback;
	private final boolean canRestore;
	private final Class<? extends Handler> handler;
	private final String niceDescription;

	InternalActionType(Class<? extends Handler> handler, String niceDescription) {
		this(false, false, false, handler, niceDescription);
	}

	InternalActionType(boolean doesCreateBlock, boolean canRollback, boolean canRestore, Class<? extends Handler> handler, String niceDescription) {
		this.doesCreateBlock = doesCreateBlock;
		this.canRollback = canRollback;
		this.canRestore = canRestore;
		this.handler = handler;
		this.niceDescription = niceDescription;
	}

	public String getName() {
		return name().toLowerCase().replaceAll("_", "-");
	}

	// @TODO dont rebuild object every time
	public ActionType get() {
		return new ActionType(getName(), doesCreateBlock, canRollback, canRestore, handler, niceDescription,true);
	}
}