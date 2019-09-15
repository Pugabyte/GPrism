package me.botsko.prism.actionlibs;

import me.botsko.prism.actions.Handler;

public class ActionType {
	private final String name;
	private final boolean doesCreateBlock;
	private final boolean canRollback;
	private final boolean canRestore;
	private final Class<? extends Handler> handler;
	private final String niceDescription;
	private final boolean internal;

	ActionType(String name, Class<? extends Handler> handler, String niceDescription) {
		this(name, false, false, false, handler, niceDescription);
	}

	ActionType(String name, boolean doesCreateBlock, boolean canRollback, boolean canRestore, Class<? extends Handler> handler, String niceDescription) {
		this.name = name;
		this.doesCreateBlock = doesCreateBlock;
		this.canRollback = canRollback;
		this.canRestore = canRestore;
		this.handler = handler;
		this.niceDescription = niceDescription;
		this.internal = false;
	}

	protected ActionType(String name, boolean doesCreateBlock, boolean canRollback, boolean canRestore, Class<? extends Handler> handler, String niceDescription, boolean internal) {
		this.name = name;
		this.doesCreateBlock = doesCreateBlock;
		this.canRollback = canRollback;
		this.canRestore = canRestore;
		this.handler = handler;
		this.niceDescription = niceDescription;
		this.internal = true;
	}

	public String getName() {
		return name;
	}

	public boolean canRollback() {
		return canRollback;
	}

	public boolean canRestore() {
		return canRestore;
	}

	public Class<? extends Handler> getHandler() {
		return handler;
	}

	public String getNiceDescription() {
		return niceDescription;
	}

	public boolean requiresHandler(Class<? extends Handler> handler) {
		return (getHandler() != null && getHandler().equals(handler));
	}

	public boolean doesCreateBlock() {
		return doesCreateBlock;
	}

	public String getFamilyName() {
		final String[] _tmp = getName().toLowerCase().split("-(?!.*-.*)");
		if (_tmp.length == 2) {
			return _tmp[0];
		}
		return getName();
	}

	public String getShortName() {
		final String[] _tmp = getName().toLowerCase().split("-(?!.*-.*)");
		if (_tmp.length == 2) {
			return _tmp[1];
		}
		return getName();
	}

	public boolean isInternal() {
		return internal;
	}

	public boolean isPrism() {
		return getName().startsWith("prism");
	}

}