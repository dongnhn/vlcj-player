package uk.co.caprica.vlcjplayer.swt.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public abstract class OptionListMenuItem<T> extends StandardMenuItem {

	public OptionListMenuItem(Menu parent, String key, final boolean dynamicGenerate) {
		super(parent, SWT.CASCADE, key, 0);
		parent.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				MenuItem self = item();
				
				if (dynamicGenerate) {
					if (self.getMenu() == null) {
						generateOptionItems();
					}
				} else {
					if (self.getMenu() != null) {
						self.getMenu().dispose();
					}
					
					generateOptionItems();
				}
			}
		});
	}

	protected void generateOptionItems() {
		List<T> options = getOptions();
		MenuItem self = item();
		if (options == null || options.isEmpty()) {
			self.setEnabled(false);
		} else {
			self.setEnabled(true);
			Menu optionsMenu = new Menu(self.getParent());
			self.setMenu(optionsMenu);
			
			for (int i = 0, len = options.size(); i < len; i++) {
				createOptionMenuItem(optionsMenu, options.get(i), i);
			}
		}
	}

	protected abstract void createOptionMenuItem(Menu menu, T option, int index);

	protected abstract List<T> getOptions();

	@Override
	public void handleEvent(Event event) {
	}
}
