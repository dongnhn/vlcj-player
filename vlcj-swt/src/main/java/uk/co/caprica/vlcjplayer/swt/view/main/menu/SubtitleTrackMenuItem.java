package uk.co.caprica.vlcjplayer.swt.view.main.menu;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.util.List;

import org.eclipse.swt.widgets.Menu;

import uk.co.caprica.vlcj.player.TrackDescription;
import uk.co.caprica.vlcjplayer.swt.view.TrackListMenuItem;

public class SubtitleTrackMenuItem extends TrackListMenuItem {

	public SubtitleTrackMenuItem(Menu parent) {
		super(parent, "menu.subtitle.item.track");
	}

	@Override
	protected void selectTrack(TrackDescription track) {
		application().mediaPlayerComponent().getMediaPlayer().setSpu(track.id());
	}

	@Override
	protected int getSelectedTrackId() {
		return application().mediaPlayerComponent().getMediaPlayer().getSpu();
	}

	@Override
	protected List<TrackDescription> getOptions() {
		return application().mediaPlayerComponent().getMediaPlayer().getSpuDescriptions();
	}

}
