package me.botsko.prism.actionlibs;

import me.botsko.prism.Prism;
import me.botsko.prism.actions.*;
import me.botsko.prism.exceptions.InvalidActionException;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;

public class HandlerRegistry<H> {
	private final HashMap<String, Class<? extends Handler>> registeredHandlers = new HashMap<>();

	public HandlerRegistry() {
		registerPrismDefaultHandlers();
	}

	protected void registerHandler(Class<? extends Handler> handlerClass) {
		final String[] names = handlerClass.getName().split("\\.");
		if (names.length > 0) {
			registeredHandlers.put(names[names.length - 1], handlerClass);
		}
	}

	public void registerCustomHandler(Plugin apiPlugin, Class<? extends Handler> handlerClass)
			throws InvalidActionException {
		// Is plugin allowed?
		@SuppressWarnings("unchecked") final ArrayList<String> allowedPlugins = (ArrayList<String>) Prism.config
				.getList("prism.tracking.api.allowed-plugins");
		if (!allowedPlugins.contains(apiPlugin.getName())) {
			throw new InvalidActionException("Registering action type not allowed. Plugin '" + apiPlugin.getName()
					+ "' is not in list of allowed plugins.");
		}
		final String[] names = handlerClass.getName().split("\\.");
		if (names.length > 0) {
			registeredHandlers.put(names[names.length - 1], handlerClass);
		}
	}

	public Handler getHandler(String name) {
		if (name != null && registeredHandlers.containsKey(name)) {
			try {
				final Class<? extends Handler> handlerClass = registeredHandlers.get(name);
				return new HandlerFactory<Handler>(handlerClass).create();
			} catch (final InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return new GenericAction();
	}

	private void registerPrismDefaultHandlers() {
		for (Class<? extends Handler> clazz : new Reflections("me.botsko.prism").getSubTypesOf(GenericAction.class)) {
			registerHandler(clazz);
		}
	}

}