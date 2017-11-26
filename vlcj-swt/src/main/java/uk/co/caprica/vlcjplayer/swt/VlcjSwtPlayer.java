package uk.co.caprica.vlcjplayer.swt;

import static uk.co.caprica.vlcjplayer.Application.application;

import javax.swing.UIManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcjplayer.event.ShutdownEvent;
import uk.co.caprica.vlcjplayer.swt.component.SwtMediaPlayerComponent;
import uk.co.caprica.vlcjplayer.swt.view.main.MainShell;

public class VlcjSwtPlayer {
	private Display display;
	private Shell mainShell;

	public static void main(String[] args) {
		// This will locate LibVLC for the vast majority of cases
		new NativeDiscovery().discover();
		VlcjSwtPlayer player = new VlcjSwtPlayer();
		setLookAndFeel();
		player.start();
	}
	
	private static void setLookAndFeel() {
        String lookAndFeelClassName;
        if (RuntimeUtil.isNix()) {
            lookAndFeelClassName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        }
        else {
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        }
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        }
        catch(Exception e) {
            // Silently fail, it doesn't matter
        }
    }
	
	public VlcjSwtPlayer() {
		Display.setAppName("VLCJ");
		display = Display.getDefault();
		mainShell = new MainShell(display).getShell();
		
		mainShell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				application().post(ShutdownEvent.INSTANCE);
			}
		});
		display.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				application().post(ShutdownEvent.INSTANCE);
				mainShell.dispose();
			}
		});
		display.addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event event) {
				SwtMediaPlayerComponent mediaPlayerComponent = application().mediaPlayerComponent();
				mediaPlayerComponent.getMediaPlayer().stop();
                mediaPlayerComponent.release();
			}
		});
	}

	private void start() {
		mainShell.open();
		while (!mainShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		System.exit(0);
	}
}
