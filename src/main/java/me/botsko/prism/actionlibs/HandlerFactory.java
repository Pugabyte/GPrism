package me.botsko.prism.actionlibs;

import me.botsko.prism.actions.Handler;

public class HandlerFactory<H> {
	final Class<? extends Handler> handlerClass;

	public HandlerFactory(Class<? extends Handler> handlerClass) {
		this.handlerClass = handlerClass;
	}

	public Handler create() throws InstantiationException, IllegalAccessException {
		return handlerClass.newInstance();
	}

}