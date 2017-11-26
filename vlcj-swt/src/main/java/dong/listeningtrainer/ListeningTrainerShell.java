package dong.listeningtrainer;

import static uk.co.caprica.vlcjplayer.Application.application;
import static uk.co.caprica.vlcjplayer.swt.SwtResource.resource;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.android.exoplayer.text.Subtitle;

import dong.listeningtrainer.ParseSubtitleThread.SubtitleParseListener;
import dong.listeningtrainer.model.ListeningTrainer;
import dong.listeningtrainer.model.ListeningTrainerTimedText;
import net.miginfocom.swt.MigLayout;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.TrackDescription;
import uk.co.caprica.vlcjplayer.swt.SwtResource;

public class ListeningTrainerShell
        implements dong.listeningtrainer.model.ListeningTrainer.Listener, SubtitleParseListener {
	private static final int ANIMATION_DURATION = 500; // milliseconds

	private final Image playIcon = newImage("play");

	private final Image pauseIcon = newImage("pause");

	private final Color black = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

	private final Color red = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	private Shell shell;

	private Label indexLabel;
	private Label repeatButton;
	private Label playPauseButton;
	private RatingComposite ratingView;
	private Text contentArea;
	private Button prevButton;
	private Button nextButton;

	private MediaPlayer mediaPlayer;
	private int savedSubtitleTrackId = -1;

	private ListeningTrainer trainer;

	public ListeningTrainerShell(Shell parent, String file) {
		this.shell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM);
		shell.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		shell.setLayout(new MigLayout("insets 10", "[shrink, left][grow, right][shrink, right]", "[shrink][shrink][grow][shrink]"));
		addViews(shell);
		shell.pack();
		Rectangle parentBounds = parent.getBounds();
		Rectangle bounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - bounds.width) / 2, parentBounds.y + (parentBounds.height - bounds.height) / 2);

		// prevent ESC
		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}
		});
		shell.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				onKeyPressed(event);
			}
		});
		shell.forceFocus();

		trainer = new ListeningTrainer();
		trainer.setListener(this);
		
		mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
		mediaPlayer.addMediaPlayerEventListener(mediaPlayerEventAdapter);
		savedSubtitleTrackId = mediaPlayer.getSpu();
		disableSubtitle(mediaPlayer);
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
		shell.addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (savedSubtitleTrackId != -1) {
					mediaPlayer.setSpu(savedSubtitleTrackId);
				}
				mediaPlayer.removeMediaPlayerEventListener(mediaPlayerEventAdapter);
			}
		});

		new ParseSubtitleThread(new File(file), this).run();
	}
	
	private void disableSubtitle(MediaPlayer mediaPlayer) {
		List<TrackDescription> spuDescriptions = mediaPlayer.getSpuDescriptions();
		TrackDescription disableSpuDescription = null;
		for (TrackDescription spuDescription : spuDescriptions) {
			if ("Disable".equalsIgnoreCase(spuDescription.description())) {
				disableSpuDescription = spuDescription;
				break;
			}
		}
		
		if (disableSpuDescription != null) {
			mediaPlayer.setSpu(disableSpuDescription.id());
		}
	}
	
	private MediaPlayerEventAdapter mediaPlayerEventAdapter = new MediaPlayerEventAdapter() {
		public void playing(MediaPlayer mediaPlayer) {
			updatePlayPauseImage();
		};
		
		public void paused(MediaPlayer mediaPlayer) {
			updatePlayPauseImage();
		};
		
		public void stopped(MediaPlayer mediaPlayer) {
			updatePlayPauseImage();
		};
		
		public void finished(MediaPlayer mediaPlayer) {
			updatePlayPauseImage();
		};
		
		public void error(MediaPlayer mediaPlayer) {
			updatePlayPauseImage();
		};
		
		public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
			ListeningTrainerTimedText text = trainer.getCurrentText();
			if (text != null) {
				if (newTime >= text.getEndTime()) {
					seekTo(text, false);
				}
			}
		};
	};

	private void addViews(Shell shell) {
		indexLabel = new Label(shell, SWT.NONE);
		indexLabel.setLayoutData("w pref+50px");

		Composite controlsPanel = new Composite(shell, SWT.NO_BACKGROUND | SWT.NO_FOCUS);
		controlsPanel.setLayoutData("span 2, shrink, align right, wrap");
		controlsPanel.setLayout(new MigLayout("insets 0"));

		repeatButton = new Label(controlsPanel, SWT.PUSH);
		repeatButton.setLayoutData("growy, w pref+10px::, h pref+10px::");
		SwtResource resource = resource("dialog.trainer.item.repeat");
		repeatButton.setImage(resource.buttonIcon());
		repeatButton.setToolTipText(resource.tooltip());
		repeatButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				repeat();
			}
		});

		playPauseButton = new Label(controlsPanel, SWT.PUSH);
		playPauseButton.setLayoutData("w pref+10px::, h pref+10px::");
		resource = resource("dialog.trainer.item.play");
		playPauseButton.setImage(resource.buttonIcon());
		playPauseButton.setToolTipText(resource.tooltip());
		playPauseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				togglePlay();
			}
		});

		ratingView = new RatingComposite(shell);
		ratingView.setLayoutData("shrink, span, wrap, align right");

		contentArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.NO_BACKGROUND);
		contentArea.setEditable(false);
		contentArea.setLayoutData("span, grow, width 350::700, height 75::");

		prevButton = new Button(shell, SWT.PUSH | SWT.NO_FOCUS);
		prevButton.setLayoutData("span 2, align right");
		resource = resource("dialog.trainer.item.prev");
		prevButton.setText(resource.name());
		prevButton.setToolTipText(resource.tooltip());
		prevButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				previous();
			}
		});

		nextButton = new Button(shell, SWT.PUSH | SWT.NO_FOCUS);
		resource = resource("dialog.trainer.item.next");
		nextButton.setText(resource.name());
		nextButton.setToolTipText(resource.tooltip());
		nextButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				next();
			}
		});
	}

	private void repeat() {
		seekTo(trainer.getCurrentText(), true);
	}

	private void togglePlay() {
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.play();
		} else {
			mediaPlayer.pause();
		}
	}

	private void next() {
		trainer.next();
	}

	private void previous() {
		trainer.previous();
	}

	private void seekTo(ListeningTrainerTimedText text, boolean autoPlay) {
		if (text != null) {
			mediaPlayer.setPosition(text.getStartTime() * 1.0f / mediaPlayer.getLength());
			if (autoPlay) {
				if (!mediaPlayer.isPlaying()) {
					mediaPlayer.play();
				}
			}
		}
	}

	private Image newImage(String name) {
		return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/buttons/" + name + ".png"));
	}
	
	private void updatePlayPauseImage() {
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				playPauseButton.setImage(mediaPlayer.isPlaying() ? pauseIcon : playIcon);
			}
		});
	}

	public Shell getShell() {
		return this.shell;
	}

	private void onKeyPressed(Event event) {
		if ((event.stateMask & SWT.CONTROL) == SWT.CONTROL) {
			switch (event.keyCode) {
			case 'r':
				repeat();
				break;
			case 'p':
				togglePlay();
				break;
			case 'n':
				next();
				break;
			case 'b':
				previous();
				break;
			case 'q':
				shell.close();
				break;
			default:
				break;
			}
		} else if ((event.stateMask & SWT.SHIFT) == SWT.SHIFT || event.stateMask == 0) {
			trainer.enter((char) event.keyCode);
		}
	}

	@Override
	public void onTextChanged(ListeningTrainerTimedText newText, String description, boolean hasNext,
	        boolean hasPrevious) {
		indexLabel.setText(description);
		prevButton.setEnabled(hasPrevious);
		nextButton.setEnabled(hasNext);
		onDisplayTextChanged(newText);
		shell.pack(true);

		seekTo(newText, true);
	}

	@Override
	public void onDisplayTextChanged(ListeningTrainerTimedText text) {
		contentArea.setForeground(black);
		contentArea.setText(text.getDisplayText());
		if (text.isCompleted()) {
			ratingView.setVisible(true);
			ratingView.setRating(text.getScorePercent());
		} else {
			ratingView.setVisible(false);
		}
	}

	@Override
	public void onWrongCharacterEntered() {
		contentArea.setForeground(red);
		shell.getDisplay().timerExec(ANIMATION_DURATION, new Runnable() {
			@Override
			public void run() {
				if (!contentArea.isDisposed()) {
					contentArea.setForeground(black);
				}
			}
		});
	}

	@Override
	public void onParseComplete(final Subtitle subtitle, Exception e) {
		if (e != null) {
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					String title = "Error";
					String message = "Cannot read subtitle file!";
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
					messageBox.setText(title);
					messageBox.setMessage(message);
					messageBox.open();
					shell.close();
				}
			});
		} else {
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					long start = (long) (mediaPlayer.getLength() * mediaPlayer.getPosition());
					long end = (mediaPlayer.getLength() - ListeningTrainerTimedText.TIME_MARGIN);
					trainer.startTraining(subtitle, start, end);
				}
			});
		}
	}
}
