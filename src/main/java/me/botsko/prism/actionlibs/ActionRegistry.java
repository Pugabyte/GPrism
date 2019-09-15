package me.botsko.prism.actionlibs;

import me.botsko.prism.Prism;
import me.botsko.prism.exceptions.InvalidActionException;
import me.botsko.prism.utils.TypeUtils;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

@SuppressWarnings("unused")
public class ActionRegistry {
	private final TreeMap<String, ActionType> registeredActions = new TreeMap<>();

	public ActionRegistry() {
		registerPrismDefaultActions();
	}

	protected void registerAction(ActionType actionType) {
		registeredActions.put(actionType.getName(), actionType);
	}

	public void registerCustomAction(Plugin apiPlugin, ActionType actionType) throws InvalidActionException {
		// Is plugin allowed?
		@SuppressWarnings("unchecked") final ArrayList<String> allowedPlugins = (ArrayList<String>) Prism.config
				.getList("prism.tracking.api.allowed-plugins");
		if (!allowedPlugins.contains(apiPlugin.getName())) {
			throw new InvalidActionException(
					"Registering action type not allowed. Plugin '" + apiPlugin.getName()
							+ "' is not in list of allowed plugins.");
		}

		// Is action type formatted right
		if (TypeUtils.subStrOccurences(actionType.getName(), "-") != 2) {
			throw new InvalidActionException(
					"Invalid action type. Custom actions must contain two hyphens.");
		}

		// Register custom action type with the db
		Prism.addActionName(actionType.getName());

		registeredActions.put(actionType.getName(), actionType);
	}

	public TreeMap<String, ActionType> getRegisteredActions() {
		return registeredActions;
	}

	public ActionType getAction(String name) {
		return registeredActions.get(name);
	}

	public ActionType getAction(InternalActionType internalActionType) {
		return registeredActions.get(internalActionType.getName());
	}

	public ArrayList<ActionType> getActionsByShortname(String name) {
		final ArrayList<ActionType> actions = new ArrayList<>();
		for (final Entry<String, ActionType> entry : registeredActions.entrySet()) {
			// Match either the name or the short name
			if (entry.getValue().getFamilyName().equals(name) || entry.getValue().getShortName().equals(name)
					|| entry.getValue().getName().equals(name)) {
				actions.add(entry.getValue());
			}
		}
		return actions;
	}

	public String[] listAll() {
		final String[] names = new String[registeredActions.size()];
		int i = 0;
		for (final Entry<String, ActionType> entry : registeredActions.entrySet()) {
			names[i] = entry.getKey();
			i++;
		}
		return names;
	}

	private void registerPrismDefaultActions() {
		for (InternalActionType internalActionType : InternalActionType.values()){
			registerAction(internalActionType.get());
		}

	}

}