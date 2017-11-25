package uk.co.caprica.vlcjplayer.swt.view.main.menu;

import static uk.co.caprica.vlcjplayer.swt.SwtApplication.application;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import uk.co.caprica.vlcjplayer.swt.view.StandardMenuItem;

public class RecentMediaMenuItem extends StandardMenuItem {
	public RecentMediaMenuItem(Menu parent) {
		super(parent, SWT.CASCADE, "menu.media.item.recent", 0);
		parent.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				generateRecentList();
			}
		});
	}

	private void generateRecentList() {
		MenuItem self = item();
		if (self.getMenu() != null) {
			self.getMenu().dispose();
		}

		List<String> recentMediaUrls = application().recentMedia();

		if (recentMediaUrls == null || recentMediaUrls.isEmpty()) {
			self.setEnabled(false);
		} else {
			self.setEnabled(true);
			Menu recentListMenu = new Menu(self.getParent());
			self.setMenu(recentListMenu);

			for (final String rm : recentMediaUrls) {
				MenuItem mediaItem = new StandardMenuItem(recentListMenu, "") {
					@Override
					public void handleEvent(Event event) {
						application().mediaPlayerComponent().getMediaPlayer().playMedia(rm);
					}
				}.item();
				mediaItem.setText(rm);
			}
			
			new MenuItem(recentListMenu, SWT.SEPARATOR);
			new StandardMenuItem(recentListMenu, "menu.media.item.recent.item.clear") {
				@Override
				public void handleEvent(Event event) {
					application().clearRecentMedia();
				}
			};
		}

	}

	@Override
	public void handleEvent(Event event) {
	}
}
