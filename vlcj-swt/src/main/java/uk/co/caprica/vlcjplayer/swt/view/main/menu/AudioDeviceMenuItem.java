package uk.co.caprica.vlcjplayer.swt.view.main.menu;

import static uk.co.caprica.vlcjplayer.swt.SwtApplication.application;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import uk.co.caprica.vlcj.player.AudioDevice;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.swt.view.OptionListMenuItem;

public class AudioDeviceMenuItem extends OptionListMenuItem<AudioDevice> {

	public AudioDeviceMenuItem(Menu parent) {
		super(parent, "menu.audio.item.device", false);
	}

	@Override
	protected void createOptionMenuItem(Menu menu, final AudioDevice device, int index) {
		final MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
		final MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
		menuItem.setText(device.getLongName());
		if (device.getDeviceId().equals(mediaPlayer.getAudioOutputDevice())) {
			menuItem.setSelection(true);
		}
		menuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (menuItem.getSelection()) {
					mediaPlayer.setAudioOutputDevice(null, device.getDeviceId());
				}
			}
		});
	}

	@Override
	protected List<AudioDevice> getOptions() {
		return application().mediaPlayerComponent().getMediaPlayer().getAudioOutputDevices();
	}
}
