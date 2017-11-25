package uk.co.caprica.vlcjplayer.swt.view.main;

import static uk.co.caprica.vlcjplayer.swt.SwtApplication.application;
import static uk.co.caprica.vlcjplayer.swt.SwtResource.resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.google.common.eventbus.Subscribe;

import net.miginfocom.swt.MigLayout;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcjplayer.event.ShutdownEvent;
import uk.co.caprica.vlcjplayer.swt.view.StandardMenuItem;

public class MainShell {
	private Shell shell;
	private VideoContentComposite videoContentComposite;

	public MainShell(Display display) {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setMenuBar(createMenuBar(shell));
		
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		shell.setLayout(layout);
		
		videoContentComposite = new VideoContentComposite(shell);
		videoContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		videoContentComposite.showDefault();
		
		Composite bottomPane = new Composite(shell, SWT.NONE);
		bottomPane.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		
		bottomPane.setLayout(new MigLayout("fill, insets 0 n n n", "[grow]", "[]0[]"));
		
		ControlButtonsComposite controlButtonsComposite = new ControlButtonsComposite(bottomPane);
		controlButtonsComposite.setLayoutData("grow, wrap");
		
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
		});
	}

	private Menu createMenuBar(final Shell shell) {
		Menu menuBar = new Menu(shell, SWT.BAR);

		MenuItem mediaItem = new MenuItem(menuBar, SWT.CASCADE);
		mediaItem.setText(resource("menu.media").name());
		final Menu mediaMenu = new Menu(mediaItem);
		mediaItem.setMenu(mediaMenu);
		// open
		new StandardMenuItem(mediaMenu, "menu.media.item.openFile", SWT.CONTROL + 'O') {
			@Override
			public void handleEvent(Event event) {
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
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
		
		return menuBar;
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
