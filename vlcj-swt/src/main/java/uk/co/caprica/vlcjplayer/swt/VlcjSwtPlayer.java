package uk.co.caprica.vlcjplayer.swt;

import static uk.co.caprica.vlcjplayer.swt.SwtApplication.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcjplayer.event.ShutdownEvent;
import uk.co.caprica.vlcjplayer.swt.component.SwtMediaPlayerComponent;
import uk.co.caprica.vlcjplayer.swt.view.main.MainShell;

public class VlcjSwtPlayer {
	private Display display;
	private Shell mainShell;

	public static void main(String[] args) {
		// This will locate LibVLC for the vast majority of cases
		new NativeDiscovery().discover();

		new VlcjSwtPlayer().start();
	}
	
	public VlcjSwtPlayer() {
		Display.setAppName("VLCJ");
		display = Display.getDefault();
		mainShell = new MainShell(display).getShell();
		
		Listener closeListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("app closed");
				application().post(ShutdownEvent.INSTANCE);
			}
		};
		mainShell.addListener(SWT.Close, closeListener);
		display.addListener(SWT.Close, closeListener);
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
