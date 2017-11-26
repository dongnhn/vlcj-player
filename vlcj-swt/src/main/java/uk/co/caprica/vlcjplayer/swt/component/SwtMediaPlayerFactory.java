package uk.co.caprica.vlcjplayer.swt.component;

import org.eclipse.swt.widgets.Composite;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.linux.LinuxVideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.mac.MacVideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.windows.WindowsVideoSurfaceAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class SwtMediaPlayerFactory extends MediaPlayerFactory {
	public SwtMediaPlayerFactory(String... libvlcArgs) {
		super(libvlcArgs);
	}
	
	@Override
	public EmbeddedMediaPlayer newEmbeddedMediaPlayer() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public EmbeddedMediaPlayer newEmbeddedMediaPlayer(FullScreenStrategy fullScreenStrategy) {
		throw new UnsupportedOperationException();
	}
	
	public CompositeVideoSurface newVideoSurface(Composite composite) {
		return new CompositeVideoSurface(composite, getVideoSurfaceAdapter());
	}
	
	static VideoSurfaceAdapter getVideoSurfaceAdapter() {
        VideoSurfaceAdapter videoSurfaceAdapter;
        if(RuntimeUtil.isNix()) {
            videoSurfaceAdapter = new LinuxVideoSurfaceAdapter();
        }
        else if(RuntimeUtil.isWindows()) {
            videoSurfaceAdapter = new WindowsVideoSurfaceAdapter();
        }
        else if(RuntimeUtil.isMac()) {
            videoSurfaceAdapter = new MacVideoSurfaceAdapter();
        }
        else {
            throw new RuntimeException("Unable to create a media player - failed to detect a supported operating system");
        }
        return videoSurfaceAdapter;
    }

	public SwtEmbeddedMediaPlayer newSwtEmbeddedMediaPlayer() {
		return new SwtEmbeddedMediaPlayer(libvlc, instance);
	}
}
