package uk.co.caprica.vlcjplayer.swt.component;

import org.eclipse.swt.widgets.Composite;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.component.AbstractMediaPlayerComponent;

public class SwtMediaPlayerComponent implements AbstractMediaPlayerComponent<Composite> {

	protected static final String[] DEFAULT_FACTORY_ARGUMENTS = {
	        "--video-title=vlcj video output",
	        "--no-snapshot-preview",
	        "--quiet-synchro",
	        "--sub-filter=logo:marq",
	        "--intf=dummy"
	    };
	
	private SwtMediaPlayerFactory mediaPlayerFactory;

	private SwtEmbeddedMediaPlayer mediaPlayer;

	private Composite composite;

	private CompositeVideoSurface videoSurface;

	public SwtMediaPlayerComponent() {
		mediaPlayerFactory = onGetMediaPlayerFactory();
		mediaPlayer = mediaPlayerFactory.newSwtEmbeddedMediaPlayer();
	}
	
	public void init(Composite composite) {
		this.composite = composite;
		videoSurface = mediaPlayerFactory.newVideoSurface(composite);
		mediaPlayer.setVideoSurface(videoSurface);
	}
	
	protected SwtMediaPlayerFactory onGetMediaPlayerFactory() {
		return new SwtMediaPlayerFactory(onGetMediaPlayerFactoryArgs());
	}

	protected String[] onGetMediaPlayerFactoryArgs() {
		return DEFAULT_FACTORY_ARGUMENTS;
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	@Override
	public void release() {
		mediaPlayer.release();
	}

	@Override
	public SwtMediaPlayerFactory getMediaPlayerFactory() {
		return mediaPlayerFactory;
	}

	@Override
	public void toggleFullScreen() {
		// TODO Auto-generated method stub
	}

	@Override
	public Composite getContentComponent() {
		return composite.getParent();
	}

	@Override
	public Composite getVideoSurface() {
		return composite;
	}

	@Override
	public void setCursorEnabled(boolean enabled) {
		// TODO Auto-generated method stub
	}

}
