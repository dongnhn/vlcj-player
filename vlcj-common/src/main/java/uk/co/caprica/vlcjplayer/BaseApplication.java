package uk.co.caprica.vlcjplayer;
/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2015 Caprica Software Limited.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.EventBus;

import uk.co.caprica.vlcjplayer.component.AbstractMediaPlayerComponent;
import uk.co.caprica.vlcjplayer.event.TickEvent;

/**
 * Global application state.
 * @param <T>
 */
public abstract class BaseApplication<T> {

	protected static final String RESOURCE_BUNDLE_BASE_NAME = "strings/vlcj-player";

	protected static final ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME);

	protected static final int MAX_RECENT_MEDIA_SIZE = 10;

	protected final EventBus eventBus;

	protected final ScheduledExecutorService tickService = Executors.newSingleThreadScheduledExecutor();

	protected final Deque<String> recentMedia = new ArrayDeque<String>(MAX_RECENT_MEDIA_SIZE);

	public static ResourceBundle resources() {
		return resourceBundle;
	}

	protected BaseApplication() {
		eventBus = new EventBus();
		tickService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				eventBus.post(TickEvent.INSTANCE);
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}

	public void subscribe(Object subscriber) {
		eventBus.register(subscriber);
	}
	
	public void post(final Object event) {
		eventBus.post(event);
	}

	public abstract AbstractMediaPlayerComponent<T> mediaPlayerComponent();
	
	public void addRecentMedia(String mrl) {
		if (!recentMedia.contains(mrl)) {
			recentMedia.addFirst(mrl);
			while (recentMedia.size() > MAX_RECENT_MEDIA_SIZE) {
				recentMedia.pollLast();
			}
		}
	}

	public List<String> recentMedia() {
		return new ArrayList<String>(recentMedia);
	}

	public void clearRecentMedia() {
		recentMedia.clear();
	}
}
