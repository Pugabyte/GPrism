package me.botsko.prism.commands;

import me.botsko.prism.Prism;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.Executor;
import me.botsko.prism.commandlibs.SubHandler;
import me.botsko.prism.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WhatCommand extends Executor {

	public WhatCommand(Prism prism) {
		super(prism, "command", "prism");
		setupCommands();
	}

	private void setupCommands() {
		addSub("what", "prism.what").setHandler(new SubHandler() {
			@Override
			public void handle(CallInfo call) {
				final ItemStack item = call.getPlayer().getItemInHand();

				call.getPlayer().sendMessage(Prism.messenger.playerHeaderMsg("Item Profile:"));

				String line1 = ChatColor.WHITE + "ID: " + ChatColor.DARK_AQUA + item.getType().toString().toLowerCase();

				call.getPlayer().sendMessage(Prism.messenger.playerMsg(line1));
				call.getPlayer().sendMessage(
						Prism.messenger.playerMsg(ChatColor.WHITE + "Full Display Name: " + ChatColor.DARK_AQUA
								+ ItemUtils.getNiceName(item)));

			}

			@Override
			public List<String> handleComplete(CallInfo call) {
				return null;
			}
		});
	}

}
