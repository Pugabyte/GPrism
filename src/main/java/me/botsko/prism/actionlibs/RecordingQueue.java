package me.botsko.prism.actionlibs;

import me.botsko.prism.actions.Handler;

import java.util.concurrent.LinkedBlockingQueue;

public class RecordingQueue {

	private static final LinkedBlockingQueue<Handler> queue = new LinkedBlockingQueue<>();

	public static int getQueueSize() {
		return queue.size();
	}

	public static void addToQueue(final Handler a) {

		if (a == null)
			return;

		a.save();

		queue.add(a);

	}

	public static LinkedBlockingQueue<Handler> getQueue() {
		return queue;
	}

}