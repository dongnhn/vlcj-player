package uk.co.caprica.vlcjplayer.swt.view.main;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import static uk.co.caprica.vlcjplayer.swt.SwtApplication.application;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.swt.view.OptionListMenuItem;

public class ChapterMenuItem extends OptionListMenuItem<String> {

	public ChapterMenuItem(Menu parent) {
		super(parent, "menu.playback.item.chapter", true);
	}

	@Override
	protected void createOptionMenuItem(Menu menu, String chapter, final int index) {
		MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
		menuItem.setText(chapter);
		final MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
		if (mediaPlayer.getChapter() == index) {
			menuItem.setSelection(true);
		} else {
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					mediaPlayer.setChapter(index);
				}
			});
		}
	}

	@Override
	protected List<String> getOptions() {
		return application().mediaPlayerComponent().getMediaPlayer().getChapterDescriptions();
	}

}
