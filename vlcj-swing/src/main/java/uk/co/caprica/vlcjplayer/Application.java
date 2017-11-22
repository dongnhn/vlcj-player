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

import java.awt.Component;

import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.version.Version;
import uk.co.caprica.vlcjplayer.component.AbstractMediaPlayerComponent;
import uk.co.caprica.vlcjplayer.component.DirectMediaPlayerJComponent;
import uk.co.caprica.vlcjplayer.component.EmbeddedMediaPlayerJComponent;
import uk.co.caprica.vlcjplayer.view.action.mediaplayer.MediaPlayerActions;

/**
 * Global application state.
 */
public final class Application extends BaseApplication<Component> {

    private final AbstractMediaPlayerComponent<Component> mediaPlayerComponent;

    private final MediaPlayerActions mediaPlayerActions;

    private static final class ApplicationHolder {
        private static final Application INSTANCE = new Application();
    }

    public static Application application() {
        return ApplicationHolder.INSTANCE;
    }

    private Application() {
    	super();
        if (RuntimeUtil.isMac() && new Version(System.getProperty("java.version")).atLeast(new Version("1.7.0"))) {
        	mediaPlayerComponent = new DirectMediaPlayerJComponent();
        } else {
        	mediaPlayerComponent = new EmbeddedMediaPlayerJComponent() {
        		@Override
        		protected String[] onGetMediaPlayerFactoryExtraArgs() {
        			return new String[] {"--no-osd"}; // Disables the display of the snapshot filename (amongst other things)
        		}
        	};
        }
        mediaPlayerActions = new MediaPlayerActions(mediaPlayerComponent.getMediaPlayer());
    }

    @Override
    public void post(final Object event) {
        // Events are always posted and processed on the Swing Event Dispatch thread
        if (SwingUtilities.isEventDispatchThread()) {
            eventBus.post(event);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    eventBus.post(event);
                }
            });
        }
    }

    @Override
    public AbstractMediaPlayerComponent<Component> mediaPlayerComponent() {
        return mediaPlayerComponent;
    }

    public MediaPlayerActions mediaPlayerActions() {
        return mediaPlayerActions;
    }
}
