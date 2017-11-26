package uk.co.caprica.vlcjplayer.swt.view.main;

import static uk.co.caprica.vlcjplayer.Application.application;
import static uk.co.caprica.vlcjplayer.swt.SwtResource.resource;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.google.common.eventbus.Subscribe;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcjplayer.event.ShowDebugEvent;
import uk.co.caprica.vlcjplayer.event.ShowEffectsEvent;
import uk.co.caprica.vlcjplayer.event.ShowMessagesEvent;
import uk.co.caprica.vlcjplayer.event.ShutdownEvent;
import uk.co.caprica.vlcjplayer.event.SnapshotImageEvent;
import uk.co.caprica.vlcjplayer.swt.view.StandardMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.AudioDeviceMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.AudioTrackMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.ChapterMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.RecentMediaMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.SpeedMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.SubtitleTrackMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.TitleTrackMenuItem;
import uk.co.caprica.vlcjplayer.swt.view.main.menu.VideoTrackMenuItem;
import uk.co.caprica.vlcjplayer.view.main.ControlsPane;
import uk.co.caprica.vlcjplayer.view.main.PositionPane;

public class MainShell {
	private Shell shell;
	private VideoContentComposite videoContentComposite;
	private PositionPane seekbar;
	private FileDialog fileDialog;

	public MainShell(Display display) {
		shell = new Shell(display, SWT.SHELL_TRIM);
		
		fileDialog = new FileDialog(shell, SWT.OPEN);
		
		shell.setMenuBar(createMenuBar(shell));
		
		shell.setLayout(new net.miginfocom.swt.MigLayout("fill, insets 0", "[grow]", "[grow]0[]0[]"));
		
		videoContentComposite = new VideoContentComposite(shell);
		videoContentComposite.setLayoutData("grow, wrap");
		videoContentComposite.showDefault();
		
		Composite bottomComposite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		bottomComposite.setLayoutData("grow, wrap, h pref+30px::");
		
		Frame bottomFrame = SWT_AWT.new_Frame(bottomComposite);
		bottomFrame.setLayout(new MigLayout("fill, insets 0", "[grow]", "[]0[]"));

		MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
		seekbar = new PositionPane(mediaPlayer);
		seekbar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
		bottomFrame.add(seekbar, "grow, wrap");

		ControlsPane playbackControls = new ControlsPane(application().mediaPlayerActions());
		playbackControls.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
		bottomFrame.add(playbackControls, "grow");
		
		shell.setMinimumSize(370, 240);
		
		handlePlayerEvents();
        application().subscribe(this);
        restorePreferences();
	}

	private void handlePlayerEvents() {
		application().mediaPlayerComponent().getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
				videoContentComposite.showVideo();
			}
			
			@Override
			public void stopped(MediaPlayer mediaPlayer) {
				videoContentComposite.showDefault();
			}
			
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				videoContentComposite.showDefault();
			}
			
			@Override
			public void error(MediaPlayer mediaPlayer) {
				videoContentComposite.showDefault();
                // TODO: show error
			}
			
			@Override
			public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
				seekbar.setDuration(newDuration);
			}
		});
	}

	private Menu createMenuBar(final Shell shell) {
		Menu menuBar = new Menu(shell, SWT.BAR);

		createMediaMenu(shell, menuBar);
		
		createPlaybackMenu(shell, menuBar);
		
		createAudioMenu(shell, menuBar);
		
		createVideoMenu(shell, menuBar);
		
		createSubtitleMenu(shell, menuBar);
		
		createToolsMenu(shell, menuBar);
		
		return menuBar;
	}

	private void createMediaMenu(final Shell shell, Menu menuBar) {
		MenuItem mediaItem = new MenuItem(menuBar, SWT.CASCADE);
		mediaItem.setText(resource("menu.media").name());
		final Menu mediaMenu = new Menu(mediaItem);
		mediaItem.setMenu(mediaMenu);
		// open
		new StandardMenuItem(mediaMenu, "menu.media.item.openFile", SWT.CONTROL + 'O') {
			@Override
			public void handleEvent(Event event) {
				String file = fileDialog.open();
				if (file != null) {
					application().addRecentMedia(file);
					application().mediaPlayerComponent().getMediaPlayer().playMedia(file);
				}
			}
		};
		
		// recent media
		new RecentMediaMenuItem(mediaMenu).item();
		
		new MenuItem(mediaMenu, SWT.SEPARATOR);
		// quit
		new StandardMenuItem(mediaMenu, "menu.media.item.quit", SWT.CONTROL + 'Q') {
			@Override
			public void handleEvent(Event event) {
				shell.close();
			}
		};
	}
	
	private void createPlaybackMenu(Shell shell, Menu menuBar) {
		MenuItem playbackItem = new MenuItem(menuBar, SWT.CASCADE);
		playbackItem.setText(resource("menu.playback").name());
		final Menu playbackMenu = new Menu(playbackItem);
		playbackItem.setMenu(playbackMenu);
		
		new TitleTrackMenuItem(playbackMenu);
		
		new ChapterMenuItem(playbackMenu);
		
		new MenuItem(playbackMenu, SWT.SEPARATOR);

		new SpeedMenuItem(playbackMenu);

		new MenuItem(playbackMenu, SWT.SEPARATOR);
		
		new StandardMenuItem(playbackMenu, "menu.playback.item.skipForward") {
			@Override
			public void handleEvent(Event event) {
				application().mediaPlayerComponent().getMediaPlayer().skip(10000);
			}
		};
		
		new StandardMenuItem(playbackMenu, "menu.playback.item.skipBackward") {
			@Override
			public void handleEvent(Event event) {
				application().mediaPlayerComponent().getMediaPlayer().skip(-10000);
			}
		};

		new MenuItem(playbackMenu, SWT.SEPARATOR);
		
		new StandardMenuItem(playbackMenu, "menu.playback.item.previousChapter") {
			@Override
			public void handleEvent(Event event) {
				application().mediaPlayerComponent().getMediaPlayer().previousChapter();
			}
		};
		
		new StandardMenuItem(playbackMenu, "menu.playback.item.nextChapter") {
			@Override
			public void handleEvent(Event event) {
				application().mediaPlayerComponent().getMediaPlayer().nextChapter();
			}
		};
		
		new MenuItem(playbackMenu, SWT.SEPARATOR);
		
		new StandardMenuItem(playbackMenu, "menu.playback.item.play") {
			@Override
			public void handleEvent(Event event) {
				MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
				if (!mediaPlayer.isPlaying()) {
					mediaPlayer.play();
				} else {
					mediaPlayer.pause();
				}
			}
		};
		
		new StandardMenuItem(playbackMenu, "menu.playback.item.stop") {
			@Override
			public void handleEvent(Event event) {
				application().mediaPlayerComponent().getMediaPlayer().stop();
			}
		};
	}

	private void createAudioMenu(Shell shell, Menu menuBar) {
		MenuItem audioItem = new MenuItem(menuBar, SWT.CASCADE);
		audioItem.setText(resource("menu.audio").name());
		final Menu audioMenu = new Menu(audioItem);
		audioItem.setMenu(audioMenu);
		
		new AudioTrackMenuItem(audioMenu);
		
		new AudioDeviceMenuItem(audioMenu);
		
		new MenuItem(audioMenu, SWT.SEPARATOR);
		
		new StandardMenuItem(audioMenu, "menu.audio.item.increaseVolume") {
			@Override
			public void handleEvent(Event event) {
				MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
				mediaPlayer.setVolume(mediaPlayer.getVolume() + 10);
			}
		};
		
		new StandardMenuItem(audioMenu, "menu.audio.item.decreaseVolume") {
			@Override
			public void handleEvent(Event event) {
				MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
				mediaPlayer.setVolume(mediaPlayer.getVolume() - 10);
			}
		};
		
		new StandardMenuItem(audioMenu, "menu.audio.item.mute") {
			@Override
			public void handleEvent(Event event) {
				application().mediaPlayerComponent().getMediaPlayer().mute();
			}
		};
	}

	private void createVideoMenu(Shell shell, Menu menuBar) {
		MenuItem videoItem = new MenuItem(menuBar, SWT.CASCADE);
		videoItem.setText(resource("menu.video").name());
		final Menu videoMenu = new Menu(videoItem);
		videoItem.setMenu(videoMenu);
		
		new VideoTrackMenuItem(videoMenu);
		
		new MenuItem(videoMenu, SWT.SEPARATOR);
		
		new StandardMenuItem(videoMenu, "menu.video.item.snapshot") {
			@Override
			public void handleEvent(Event event) {
				MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
				BufferedImage image = mediaPlayer.getSnapshot();
		        if (image != null) {
		            application().post(new SnapshotImageEvent(image));
		        }
			}
		};
	}

	private void createSubtitleMenu(Shell shell, Menu menuBar) {
		MenuItem subtitleItem = new MenuItem(menuBar, SWT.CASCADE);
		subtitleItem.setText(resource("menu.subtitle").name());
		final Menu subtitleMenu = new Menu(subtitleItem);
		subtitleItem.setMenu(subtitleMenu);
		
		new StandardMenuItem(subtitleMenu, "menu.subtitle.item.addSubtitleFile") {
			@Override
			public void handleEvent(Event event) {
				String file = fileDialog.open();
				if (file != null) {
					application().mediaPlayerComponent().getMediaPlayer().setSubTitleFile(file);
				}
			}
		};
		
		new SubtitleTrackMenuItem(subtitleMenu);
	}

	private void createToolsMenu(Shell shell, Menu menuBar) {
		MenuItem toolsItem = new MenuItem(menuBar, SWT.CASCADE);
		toolsItem.setText(resource("menu.tools").name());
		final Menu toolsMenu = new Menu(toolsItem);
		toolsItem.setMenu(toolsMenu);
		
		new StandardMenuItem(toolsMenu, "menu.tools.item.effects", SWT.CONTROL + 'E') {
			@Override
			public void handleEvent(Event event) {
				application().post(ShowEffectsEvent.INSTANCE);
			}
		};
		
		new StandardMenuItem(toolsMenu, "menu.tools.item.messages", SWT.CONTROL + 'M') {
			@Override
			public void handleEvent(Event event) {
				application().post(ShowMessagesEvent.INSTANCE);
			}
		};
		
		new MenuItem(toolsMenu, SWT.SEPARATOR);
		
		new StandardMenuItem(toolsMenu, "menu.tools.item.debug", (SWT.CONTROL | SWT.SHIFT) + 'D') {
			@Override
			public void handleEvent(Event event) {
				application().post(ShowDebugEvent.INSTANCE);
			}
		};
	}

	public Shell getShell() {
		return this.shell;
	}
	
	@Subscribe
    public void onShutdown(ShutdownEvent event) {
		savePreferences();
    }

	private void savePreferences() {
		Preferences prefs = Preferences.userNodeForPackage(MainShell.class);
		String recentMedia;
        List<String> mrls = application().recentMedia();
        if (!mrls.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String mrl : mrls) {
                if (sb.length() > 0) {
                    sb.append('|');
                }
                sb.append(mrl);
            }
            recentMedia = sb.toString();
        }
        else {
            recentMedia = "";
        }
        prefs.put("recentMedia", recentMedia);
	}
	
	private void restorePreferences() {
        Preferences prefs = Preferences.userNodeForPackage(MainShell.class);
        String recentMedia = prefs.get("recentMedia", "");
        if (recentMedia.length() > 0) {
            List<String> mrls = Arrays.asList(prefs.get("recentMedia", "").split("\\|"));
            Collections.reverse(mrls);
            for (String mrl : mrls) {
                application().addRecentMedia(mrl);
            }
        }
    }
}
