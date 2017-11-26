package uk.co.caprica.vlcjplayer.component;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public interface AbstractMediaPlayerComponent<T> {

	MediaPlayer getMediaPlayer();

	void release();

	MediaPlayerFactory getMediaPlayerFactory();

	void toggleFullScreen();

	T getContentComponent();

	T getVideoSurface();

	void setCursorEnabled(boolean enabled);
}
