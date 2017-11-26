package uk.co.caprica.vlcjplayer.swt.view;

import static uk.co.caprica.vlcjplayer.Application.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

public class BaseComposite extends Composite implements MediaPlayerEventListener {
	public BaseComposite(Composite parent, int style) {
		super(parent, style);
		application().mediaPlayerComponent().getMediaPlayer().addMediaPlayerEventListener(this);
		addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event event) {
				application().mediaPlayerComponent().getMediaPlayer().removeMediaPlayerEventListener(BaseComposite.this);
			}
		});
	}

	@Override
	public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
	}

	@Override
	public void opening(MediaPlayer mediaPlayer) {
	}

	@Override
	public void buffering(MediaPlayer mediaPlayer, float newCache) {
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
	}

	@Override
	public void forward(MediaPlayer mediaPlayer) {
	}

	@Override
	public void backward(MediaPlayer mediaPlayer) {
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
	}

	@Override
	public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
	}

	@Override
	public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
	}

	@Override
	public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
	}

	@Override
	public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
	}

	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
	}

	@Override
	public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
	}

	@Override
	public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
	}

	@Override
	public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
	}

	@Override
	public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
	}

	@Override
	public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
	}

	@Override
	public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
	}

	@Override
	public void corked(MediaPlayer mediaPlayer, boolean corked) {
	}

	@Override
	public void muted(MediaPlayer mediaPlayer, boolean muted) {
	}

	@Override
	public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
	}

	@Override
	public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
	}

	@Override
	public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
	}

	@Override
	public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
	}

	@Override
	public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
	}

	@Override
	public void mediaFreed(MediaPlayer mediaPlayer) {
	}

	@Override
	public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
	}

	@Override
	public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {
	}

	@Override
	public void newMedia(MediaPlayer mediaPlayer) {
	}

	@Override
	public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
	}

	@Override
	public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
	}

	@Override
	public void endOfSubItems(MediaPlayer mediaPlayer) {
	}

}
