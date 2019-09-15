package me.botsko.prism.parameters;

import me.botsko.prism.actionlibs.QueryParameters;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class BlockParameter extends SimplePrismParameterHandler {
	public BlockParameter() {
		super("Block", Pattern.compile("[\\w,:]+"), "b");
	}

	@Override
	public void process(QueryParameters query, String alias, String input, CommandSender sender) {
		final String[] blocks = input.split(",");

		if (blocks.length > 0) {
			for (final String b : blocks) {
				Material material = Material.matchMaterial(b);
				if (material != null) {
					query.addBlockFilter(material);
				}
				throw new IllegalArgumentException("Invalid block name '" + b + "'. Try /pr ? for help");
			}
		}
	}

}