package uk.co.caprica.vlcjplayer.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import uk.co.caprica.vlcj.player.TrackDescription;

public abstract class TrackListMenuItem extends OptionListMenuItem<TrackDescription> {

	public TrackListMenuItem(Menu parent, String key) {
		super(parent, key, true);
	}

	@Override
	protected void createOptionMenuItem(Menu menu, final TrackDescription track, int index) {
		MenuItem item = new MenuItem(menu, SWT.CHECK);
		item.setText(track.description());
		if (getSelectedTrackId() == track.id()) {
			item.setSelection(true);
		} else {
			item.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					selectTrack(track);
				}
			});
		}
	}
	
	protected abstract void selectTrack(TrackDescription track);

	protected abstract int getSelectedTrackId();
}
