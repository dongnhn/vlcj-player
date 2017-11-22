package uk.co.caprica.vlcjplayer.component;

import java.awt.Component;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class EmbeddedMediaPlayerJComponent extends EmbeddedMediaPlayerComponent implements AbstractMediaPlayerComponent<Component> {

	@Override
	public void toggleFullScreen() {
		getMediaPlayer().toggleFullScreen();
	}

	@Override
	public Component getContentComponent() {
		return this;
	}
}
