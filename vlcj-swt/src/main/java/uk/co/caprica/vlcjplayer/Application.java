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

package uk.co.caprica.vlcjplayer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import uk.co.caprica.vlcjplayer.BaseApplication;
import uk.co.caprica.vlcjplayer.swt.component.SwtMediaPlayerComponent;
import uk.co.caprica.vlcjplayer.view.action.mediaplayer.MediaPlayerActions;

/**
 * Global application state.
 */
public final class Application extends BaseApplication<Composite> {

    private SwtMediaPlayerComponent mediaPlayerComponent;
	private MediaPlayerActions mediaPlayerActions;

    private static final class ApplicationHolder {
        private static final Application INSTANCE = new Application();
    }

    public static Application application() {
        return ApplicationHolder.INSTANCE;
    }

    private Application() {
    	super();
    	mediaPlayerComponent = new SwtMediaPlayerComponent();
    }

    @Override
    public void post(final Object event) {
        Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				eventBus.post(event);
			}
		});
    }

    @Override
    public SwtMediaPlayerComponent mediaPlayerComponent() {
        return mediaPlayerComponent;
    }
    
    public MediaPlayerActions mediaPlayerActions() {
    	if (mediaPlayerActions == null) {
    		mediaPlayerActions = new MediaPlayerActions(mediaPlayerComponent.getMediaPlayer());
    	}
        return mediaPlayerActions;
    }
}
