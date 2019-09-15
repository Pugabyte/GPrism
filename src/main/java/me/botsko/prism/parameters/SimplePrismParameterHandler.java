package me.botsko.prism.parameters;

import me.botsko.prism.actionlibs.QueryParameters;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public abstract class SimplePrismParameterHandler implements PrismParameterHandler {
	private final String name;
	private final Pattern inputMatcher;
	private final Set<String> aliases;

	private String permission;

	public SimplePrismParameterHandler(String name, String... aliases) {
		this(name, null, aliases);
	}

	public SimplePrismParameterHandler(String name, Pattern inputMatcher, String... aliases) {
		this.name = name;
		this.inputMatcher = inputMatcher;
		// Set aliases to name + aliases
		this.aliases = new HashSet<>(Arrays.asList(aliases));
		if (this.aliases.isEmpty()) {
			this.aliases.add(this.name.toLowerCase());
		}
		permission = "prism.parameters." + name.toLowerCase();
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public String[] getHelp() {
		return new String[0];
	}

	public String getPermission() {
		return permission;
	}

	protected void setPermission(String permission) {
		this.permission = permission;
	}

	protected abstract void process(QueryParameters query, String alias, String input, CommandSender sender);

	@Override
	public final void process(QueryParameters query, String parameter, CommandSender sender) {
		// Should never fail, applicable is called first
		final String[] split = parameter.split(":", 2);
		final String alias = split[0];
		final String input = split[1];
		if (inputMatcher != null && !inputMatcher.matcher(input).matches()) {
			throw new IllegalArgumentException(
					"Invalid syntax for parameter " + input);
		}

		process(query, alias, input, sender);
	}

	@Override
	public final boolean applicable(String parameter, CommandSender sender) {
		final String[] split = parameter.split(":", 2);
		if (split.length != 2)
			return false;
		final String alias = split[0];
		return aliases.contains(alias);
	}

	@Override
	public final List<String> tabComplete(String partialParameter, CommandSender sender) {
		// Should never fail, applicable is called first
		final String[] split = partialParameter.split(":", 2);
		final String alias = split[0];
		final String input = split[1];
		final List<String> completions = tabComplete(alias, input, sender);
		if (completions == null)
			return Collections.emptyList();
		final List<String> edited = new ArrayList<>(completions.size());
		for (final String completion : completions) {
			edited.add(alias + ":" + completion);
		}
		return edited;
	}

	protected List<String> tabComplete(String alias, String partialParameter, CommandSender sender) {
		return null;
	}

	@Override
	public final boolean hasPermission(String parameter, Permissible permissible) {
		if (permissible == null) return true;
		return permissible.hasPermission(permission);
	}

}
