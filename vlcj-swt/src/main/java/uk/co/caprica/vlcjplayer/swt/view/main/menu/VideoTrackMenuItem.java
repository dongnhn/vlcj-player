package uk.co.caprica.vlcjplayer.swt.view.main.menu;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.util.List;

import org.eclipse.swt.widgets.Menu;

import uk.co.caprica.vlcj.player.TrackDescription;
import uk.co.caprica.vlcjplayer.swt.view.TrackListMenuItem;

public class VideoTrackMenuItem extends TrackListMenuItem {
	public VideoTrackMenuItem(Menu parent) {
		super(parent, "menu.video.item.track");
	}

	@Override
	protected void selectTrack(TrackDescription track) {
		application().mediaPlayerComponent().getMediaPlayer().setVideoTrack(track.id());
	}

	@Override
	protected int getSelectedTrackId() {
		return application().mediaPlayerComponent().getMediaPlayer().getVideoTrack();
	}

	@Override
	protected List<TrackDescription> getOptions() {
		return application().mediaPlayerComponent().getMediaPlayer().getVideoDescriptions();
	}

}
