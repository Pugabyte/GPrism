package me.botsko.prism.actionlibs;

import me.botsko.prism.actions.Handler;
import me.botsko.prism.utils.BlockUtils;

import me.botsko.prism.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;

public class ActionMessage {
	protected final Handler a;
	private boolean showExtended = false;
	private int index = 0;

	public ActionMessage(Handler a) {
		this.a = a;
	}

	public void showExtended() {
		showExtended = true;
	}

	public void setResultIndex(int index) {
		this.index = index;
	}

	public final int getResultIndex() {
		return index;
	}

	/**
	 * Here, we don't use formatting or anything, we just use a regular message raw.
	 *
	 * This will automatically show extended information, as this can be passed to a
	 * pastebin service.
	 *
	 * @return
	 */
	public String getRawMessage() {
		final StringBuilder msg = new StringBuilder();
		ActionType action = a.getActionType();

		msg.append((action.doesCreateBlock() || action.getName().equals("item-insert")
				|| action.getName().equals("sign-change")) ? "+" : "-");
		msg.append(" #").append(a.getId());
		msg.append(" ").append(a.getSourceName());
		msg.append(" ").append(action.getName());
		msg.append(" ").append(a.getMaterial());
		msg.append(BlockUtils.dataString(a.getBlockData()));

		if (action.getHandler() != null) {
			if (!a.getNiceName().isEmpty())
				msg.append(" (").append(a.getNiceName()).append(")");
		}
		else {
			// We should really improve this, but this saves me from having to
			// make
			// a custom handler.
			if (action.getName().equals("lava-bucket")) {
				msg.append(" (lava)");
			}
			else if (action.getName().equals("water-bucket")) {
				msg.append(" (water)");
			}
		}
		if (a.getAggregateCount() > 1) {
			msg.append(" x").append(a.getAggregateCount());
		}
		msg.append(" ").append(a.getDisplayDate());
		msg.append(" ").append(a.getDisplayTime().toLowerCase());
		Location l = a.getLocation();
		msg.append(" - ").append(l.getWorld().getName()).append(" @ ").append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ());
		return msg.toString();
	}

	public BaseComponent getJSONMessage() {

		final ChatColor highlight = ChatColor.DARK_AQUA;
		// Strikethrough when was rollback
		final boolean strike = a.getWasRollback() == 1;

		TextComponent textComponent = new TextComponent();
		TextComponent extraComponent;

		// Positive/negative prefixing
		if( a.getActionType().doesCreateBlock() || a.getActionType().getName().equals( "item-insert" )
				|| a.getActionType().getName().equals( "sign-change" ) ) {
			extraComponent = new TextComponent(" + ");
			extraComponent.setColor(ChatColor.GREEN);
		} else {
			extraComponent = new TextComponent(" - ");
			extraComponent.setColor(ChatColor.RED);
		}
		textComponent.addExtra(extraComponent);

		// Result index for teleporting
		if ( index > 0 ) {
			extraComponent = new TextComponent("[" + index + "] ");
			extraComponent.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pr tp id:" + a.getId()));
			extraComponent.setHoverEvent(
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {
							new TextComponent("Click to teleport to " + a.getId() + "\n" + a.getWorld().getName()
									+ " @ " + a.getX() + " " + a.getY() + " " + a.getZ()) }));
			textComponent.addExtra(extraComponent);
		}

		// Who
		extraComponent = new TextComponent((a.getPlayer() != null) ? a.getPlayer().getName() : a.getSourceName());
		extraComponent.setColor(highlight);
		extraComponent.setStrikethrough(strike);
		textComponent.addExtra(extraComponent);

		// Description of event
		extraComponent = new TextComponent(" " + a.getActionType().getNiceDescription());
		extraComponent.setColor(ChatColor.WHITE);
		extraComponent.setStrikethrough(strike);
		if( a.getActionType().getHandler() != null ) {
			if( !a.getNiceName().isEmpty() ) {
				TextComponent extraExtra = new TextComponent(" " + a.getNiceName());
				extraExtra.setColor(highlight);
				extraExtra.setStrikethrough(strike);
				extraComponent.addExtra(extraExtra);
			}
		} else {
			// We should really improve this, but this saves me from having to
			// make a custom handler.
			String niceBucket = null;
			if (a.getActionType().equals(InternalActionType.LAVA_BUCKET.get())) {
				niceBucket = " lava";
			} else if (a.getActionType().equals(InternalActionType.WATER_BUCKET.get())) {
				niceBucket = " water";
			}
			if ( niceBucket != null ) {
				TextComponent extraExtra = new TextComponent(" " + a.getNiceName());
				extraExtra.setColor(highlight);
				extraExtra.setStrikethrough(strike);
				extraComponent.addExtra(extraExtra);
			}
		}
		// Action type reminder
		TextComponent extraExtra = new TextComponent("a:" + a.getActionType().getShortName());
		extraExtra.setColor(ChatColor.GRAY);
		extraComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { extraExtra }));
		textComponent.addExtra(extraComponent);

		if( showExtended ) {
			extraComponent = new TextComponent(ItemUtils.getNiceName(a.getMaterial()));
			extraComponent.setColor(ChatColor.GRAY);
			textComponent.addExtra(extraComponent);
		}

		// Aggregate count
		if( a.getAggregateCount() > 1 ) {
			extraComponent = new TextComponent(" x" + a.getAggregateCount());
			extraComponent.setColor(ChatColor.GREEN);
			extraComponent.setStrikethrough(strike);
			textComponent.addExtra(extraComponent);
		}

		// Time since
		if( !a.getTimeSince().isEmpty() ) {
			extraComponent = new TextComponent(" " + a.getTimeSince());
			extraComponent.setColor(ChatColor.WHITE);
			extraComponent.setStrikethrough(strike);
			// Additional date data (line2 of non-JSON message)
			extraExtra = new TextComponent(a.getDisplayDate() + " " + a.getDisplayTime().toLowerCase());
			extraExtra.setColor(ChatColor.GRAY);
			extraComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { extraExtra }));
			textComponent.addExtra(extraComponent);
		}

		return textComponent;
	}

	public String[] getMessage() {

		String[] msg = new String[1];
		if (showExtended) {
			msg = new String[2];
		}

		final ChatColor highlight = ChatColor.DARK_AQUA;

		String line1 = "";

		// +/-
		line1 += getPosNegPrefix();

		// Result index for teleporting
		if (index > 0) {
			line1 += ChatColor.GRAY + " [" + index + "] ";
		}

		// Who
		line1 += highlight + a.getSourceName();

		String description = a.getCustomDescription();
		ActionType action = a.getActionType();

		if (description == null)
			description = action.getNiceDescription();

		// Description of event
		line1 += " " + ChatColor.WHITE + description;
		if (action.getHandler() != null) {
			if (!a.getNiceName().isEmpty())
				line1 += " " + highlight + a.getNiceName();
		}
		else {
			// We should really improve this, but this saves me from having to
			// make
			// a custom handler.
			if (action.getName().equals("lava-bucket")) {
				line1 += " " + highlight + "lava";
			}
			else if (action.getName().equals("water-bucket")) {
				line1 += " " + highlight + "water";
			}
		}

		if (showExtended) {
			line1 += " " + a.getMaterial() + BlockUtils.dataString(a.getBlockData());
		}

		// Aggregate count
		if (a.getAggregateCount() > 1) {
			line1 += ChatColor.GREEN + " x" + a.getAggregateCount();
		}

		// Time since
		if (!a.getTimeSince().isEmpty()) {
			line1 += ChatColor.WHITE + " " + a.getTimeSince();
		}

		// Action type reminder
		line1 += " " + ChatColor.GRAY + "(a:" + action.getShortName() + ")";

		// Line 2
		String line2 = ChatColor.GRAY + " --";

		line2 += ChatColor.GRAY + " " + a.getId() + " - ";

		// Date & Time
		if (showExtended) {
			line2 += ChatColor.GRAY + a.getDisplayDate();
			line2 += " " + ChatColor.GRAY + a.getDisplayTime().toLowerCase();
			Location l = a.getLocation();
			line2 += " - " + l.getWorld().getName() + " @ " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " ";
		}

		msg[0] = line1;
		if (showExtended) {
			msg[1] = line2;
		}

		return msg;

	}

	protected String getPosNegPrefix() {

		if (a.getActionType().doesCreateBlock() || a.getActionType().getName().equals("item-insert")
				|| a.getActionType().getName().equals("sign-change")) {
			return ChatColor.GREEN + " + " + ChatColor.WHITE;
		}
		else {
			return ChatColor.RED + " - " + ChatColor.WHITE;
		}
	}
}