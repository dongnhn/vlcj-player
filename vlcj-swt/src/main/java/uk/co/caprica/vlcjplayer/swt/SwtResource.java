package uk.co.caprica.vlcjplayer.swt;

import static uk.co.caprica.vlcjplayer.BaseApplication.resources;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public final class SwtResource {
	private String id;

	public static SwtResource resource(String id) {
		return new SwtResource(id);
	}
	
	private SwtResource(String id) {
		this.id = id;
	}
	
	public String name() {
        if (resources().containsKey(id)) {
            return resources().getString(id);
        }
        else {
            return null;
        }
    }
	
	public Integer mnemonic() {
        String key = id + ".mnemonic";
        if (resources().containsKey(key)) {
            return new Integer(resources().getString(key).charAt(0));
        }
        else {
            return null;
        }
    }
	
	public String tooltip() {
		String key = id + ".tooltip";
		if (resources().containsKey(key)) {
			return resources().getString(key);
		} else {
			return null;
		}
    }
	
	public Image menuIcon() {
		String key = id + ".menuIcon";
		if (resources().containsKey(key)) {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/actions/" + resources().getString(key) + ".png"));
		} else {
			return null;
		}
	}
}
