package uk.co.caprica.vlcjplayer.swt.view.main.menu;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.swt.view.OptionListMenuItem;

import static uk.co.caprica.vlcjplayer.Application.application;
import static uk.co.caprica.vlcjplayer.swt.SwtResource.resource;

public class SpeedMenuItem extends OptionListMenuItem<Float> {

	List<Float> rates = Arrays.asList(4f, 2f, 1f, 0.5f, 0.25f);
	List<String> names = Arrays.asList(
			resource("menu.playback.item.speed.item.x4").name(),
			resource("menu.playback.item.speed.item.x2").name(),
			resource("menu.playback.item.speed.item.normal").name(),
			resource("menu.playback.item.speed.item./2").name(),
			resource("menu.playback.item.speed.item./4").name()
			);
	
	public SpeedMenuItem(Menu parent) {
		super(parent, "menu.playback.item.speed", false);
	}

	@Override
	protected void createOptionMenuItem(Menu menu, final Float rate, int index) {
		MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
		menuItem.setText(names.get(index));
		final MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
		if (rate.equals(mediaPlayer.getRate())) {
			menuItem.setSelection(true);
		} else {
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					mediaPlayer.setRate(rate);
				}
			});
		}
	}

	@Override
	protected List<Float> getOptions() {
		return rates;
	}

}
