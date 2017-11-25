package uk.co.caprica.vlcjplayer.swt.view.main.menu;

import static uk.co.caprica.vlcjplayer.swt.SwtApplication.application;

import java.util.List;

import org.eclipse.swt.widgets.Menu;

import uk.co.caprica.vlcj.player.TrackDescription;
import uk.co.caprica.vlcjplayer.swt.view.TrackListMenuItem;

public class AudioTrackMenuItem extends TrackListMenuItem {

	public AudioTrackMenuItem(Menu parent) {
		super(parent, "menu.audio.item.track");
	}

	@Override
	protected void selectTrack(TrackDescription track) {
		application().mediaPlayerComponent().getMediaPlayer().setAudioTrack(track.id());
	}

	@Override
	protected int getSelectedTrackId() {
		return application().mediaPlayerComponent().getMediaPlayer().getAudioTrack();
	}

	@Override
	protected List<TrackDescription> getOptions() {
		return application().mediaPlayerComponent().getMediaPlayer().getAudioDescriptions();
	}
}
