package uk.co.caprica.vlcjplayer.swt.view.main;

import static uk.co.caprica.vlcjplayer.Application.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class VideoContentComposite extends Composite {

	private StackLayout stackLayout;
	private Label defaultContent;
	private Composite videoContent;

	VideoContentComposite(Shell shell) {
		super(shell, SWT.NONE);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		stackLayout = new StackLayout();
		setLayout(stackLayout);
		
		defaultContent = new Label(this, SWT.NO_BACKGROUND);
		defaultContent.setImage(new Image(getDisplay(), getClass().getResourceAsStream("/vlcj-logo.png")));
		
		videoContent = new Composite(this, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		application().mediaPlayerComponent().init(videoContent);
	}

	public void showDefault() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				stackLayout.topControl = defaultContent;
				layout();
			}
		});
	}
	
	public void showVideo() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				stackLayout.topControl = videoContent;
				layout();
			}
		});
	}
}
