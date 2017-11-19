package uk.co.caprica.vlcjplayer.component;

import java.awt.Component;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public interface AbstractMediaPlayerJComponent {

	MediaPlayer getMediaPlayer();

	void release();

	MediaPlayerFactory getMediaPlayerFactory();

	void toggleFullScreen();

	Component getContentComponent();

	Component getVideoSurface();

	void setCursorEnabled(boolean enabled);
}
