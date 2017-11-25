package uk.co.caprica.vlcjplayer.swt.view;

import static uk.co.caprica.vlcjplayer.swt.SwtResource.resource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import uk.co.caprica.vlcjplayer.swt.SwtResource;

public abstract class StandardMenuItem implements Listener {
	private MenuItem menuItem;

	public StandardMenuItem(Menu parent, String key) {
		this(parent, SWT.NONE, key, 0);
	}
	
	public StandardMenuItem(Menu parent, String key, int shortcut) {
		this(parent, SWT.NONE, key, shortcut);
	}

	public StandardMenuItem(Menu parent, int style, String key, int shortcut) {
		menuItem = new MenuItem(parent, style);
		SwtResource resource = resource(key);
		if (resource.name() != null) {
			menuItem.setText(resource.name());
		}
		menuItem.setAccelerator(shortcut);
		menuItem.setImage(resource.menuIcon());
		menuItem.addListener(SWT.Selection, this);
	}
	
	public MenuItem item() {
		return menuItem;
	}
}
