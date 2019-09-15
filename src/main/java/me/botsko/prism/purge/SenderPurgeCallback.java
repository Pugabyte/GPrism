package me.botsko.prism.purge;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import org.bukkit.command.CommandSender;

public class SenderPurgeCallback implements PurgeCallback {

	/**
	 *
	 */
	private CommandSender sender;

	/**
	 * Simply log the purges, being done automatically
	 */
	@Override
	public void cycle(QueryParameters param, int cycle_rows_affected, int total_records_affected, boolean cycle_complete) {
		if (sender == null)
			return;

		if (cycle_rows_affected > 0) {
			sender.sendMessage(Prism.messenger.playerSubduedHeaderMsg("Purge cycle cleared " + cycle_rows_affected
					+ " records."));
		}

		if (cycle_complete) {
			sender.sendMessage(Prism.messenger.playerHeaderMsg(total_records_affected + " records have been purged."));
		}
	}

	/**
	 * @param sender
	 */
	public void setSender(CommandSender sender) {
		this.sender = sender;
	}

}