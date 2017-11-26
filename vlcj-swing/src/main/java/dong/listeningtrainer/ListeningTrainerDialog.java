package dong.listeningtrainer;


import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.google.android.exoplayer.text.Subtitle;

import dong.listeningtrainer.ParseSubtitleThread.SubtitleParseListener;
import dong.listeningtrainer.model.ListeningTrainerTimedText;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.TrackDescription;
import uk.co.caprica.vlcjplayer.view.action.Resource;
import uk.co.caprica.vlcjplayer.view.action.StandardAction;
import uk.co.caprica.vlcjplayer.view.action.mediaplayer.PlayAction;

public class ListeningTrainerDialog extends JDialog implements SubtitleParseListener, KeyListener {
	
	private static final int ANIMATION_DURATION = 500; //milliseconds
	
	private final Icon playIcon = newIcon("play");

    private final Icon pauseIcon = newIcon("pause");
    
	private JLabel indexLabel;
	private JButton repeatButton;
	private JButton playPauseButton;
	private RatingView ratingView;
	private JTextArea contentArea;
	private JButton prevButton;
	private JButton nextButton;
	
	private MediaPlayer mediaPlayer;

	private int savedSubtitleTrackId = -1;

	private List<ListeningTrainerTimedText> listTimedText;
	
	private ListeningTrainerTimedText currentTimedText;
	private int currentIndex;
	
	private Timer changeTextColorAnimator;

	public ListeningTrainerDialog(Window window, File file) {
		super(window, "Listening Trainer", ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
		
		setLayout(new MigLayout("insets 10", "[shrink, left][grow, right][shrink, right]"));
		getContentPane().setBackground(Color.WHITE);
		addComponents();
		pack();
		setResizable(false);
		
		changeTextColorAnimator = new Timer(ANIMATION_DURATION, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentArea.setForeground(Color.BLACK);
			}
		});
		changeTextColorAnimator.setRepeats(false);
		
		getRootPane().setFocusable(true);
		getRootPane().addKeyListener(this);
		
		mediaPlayer.addMediaPlayerEventListener(mediaPlayerEventAdapter);

		savedSubtitleTrackId = mediaPlayer.getSpu();
		disableSubtitle(mediaPlayer);

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
		
		new ParseSubtitleThread(file, this).run();
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

	private void addComponents() {
		indexLabel = new JLabel();
		
		repeatButton = new BigButton();
		repeatButton.setFocusable(false);
		repeatButton.setAction(new StandardAction(Resource.resource("dialog.trainer.item.repeat")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				seekToCurrentTextTime(true);
			}
		});
		
		playPauseButton = new BigButton();
		playPauseButton.setFocusable(false);
		playPauseButton.setAction(new PlayAction(Resource.resource("dialog.trainer.item.play"), mediaPlayer));
		
		ratingView = new RatingView();
		
		contentArea = new JTextArea();
		contentArea.setEditable(false);
		contentArea.setFocusable(false);
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);
		
		prevButton = new JButton();
		prevButton.setFocusable(false);
		prevButton.setAction(new StandardAction(Resource.resource("dialog.trainer.item.prev")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentIndex > 0) {
					updateCurrentIndex(currentIndex - 1);
				}
			}
		});
		nextButton = new JButton();
		nextButton.setFocusable(false);
		nextButton.setAction(new StandardAction(Resource.resource("dialog.trainer.item.next")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentIndex + 1 < listTimedText.size()) {
					updateCurrentIndex(currentIndex + 1);
				}
			}
		});
		
		add(indexLabel);
		JPanel controlsPanel = new JPanel(new MigLayout("insets 0"));
		controlsPanel.setOpaque(false);
		controlsPanel.add(repeatButton, "growy");
		controlsPanel.add(playPauseButton);
		add(controlsPanel, "span 2, shrink, align right, wrap");
		add(ratingView, "shrink, span, wrap, align right");
		add(contentArea, "span, grow, width 350::, height 75::");
		add(prevButton, "span 2, align right");
		add(nextButton);
	}
	
	private MediaPlayerEventAdapter mediaPlayerEventAdapter = new MediaPlayerEventAdapter() {
		@Override
		public void playing(MediaPlayer mediaPlayer) {
			playPauseButton.setIcon(pauseIcon);
		}
		
		@Override
		public void paused(MediaPlayer mediaPlayer) {
		    playPauseButton.setIcon(playIcon);
		}
		
		@Override
		public void stopped(MediaPlayer mediaPlayer) {
		    playPauseButton.setIcon(playIcon);
		}
		
		@Override
		public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
			if (currentTimedText != null) {
				if (newTime >= currentTimedText.getEndTime()) {
					seekToCurrentTextTime(false);
				}
			}
		}
	};

	private void seekToCurrentTextTime(boolean autoPlay) {
		mediaPlayer.setPosition(currentTimedText.getStartTime() * 1.0f / mediaPlayer.getLength());
		if (autoPlay) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.play();
			}
		}
	}
	
	private static class BigButton extends JButton {

        private BigButton() {
            setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            setHideActionText(true);
        }
    }

    private static class StandardButton extends JButton {

        private StandardButton() {
            setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            setHideActionText(true);
        }
    }
    
    private Icon newIcon(String name) {
        return new ImageIcon(getClass().getResource("/icons/buttons/" + name + ".png"));
    }

	@Override
	public void dispose() {
		mediaPlayer.removeMediaPlayerEventListener(mediaPlayerEventAdapter);
		if (savedSubtitleTrackId != -1) {
			mediaPlayer.setSpu(savedSubtitleTrackId);
		}
		super.dispose();
	}
	
	private void updateCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
		this.currentTimedText = listTimedText.get(currentIndex);
		
		indexLabel.setText(String.format("#%s/%s", currentIndex + 1, listTimedText.size()));
		contentArea.setText(currentTimedText.getDisplayText());
		prevButton.setEnabled(currentIndex > 0);
		nextButton.setEnabled(currentIndex + 1 < listTimedText.size());
		updateCompleteStatus();
		pack();
		
		seekToCurrentTextTime(true);
	}

	private void updateCompleteStatus() {
		if (currentTimedText.isCompleted()) {
			ratingView.setVisible(true);
			ratingView.setRating(currentTimedText.getScorePercent());
		} else {
			ratingView.setVisible(false);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		if (!currentTimedText.isCompleted() && c != KeyEvent.CHAR_UNDEFINED && Character.isLetterOrDigit(c)) {
			if (currentTimedText.inputChar(c)) {
				contentArea.setForeground(Color.BLACK);
				contentArea.setText(currentTimedText.getDisplayText());
				updateCompleteStatus();
			} else {
				contentArea.setForeground(Color.RED);
				changeTextColorAnimator.start();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
			int keyCode = e.getKeyCode();
			
			switch (keyCode) {
			case KeyEvent.VK_N:
				nextButton.doClick();
				break;
			case KeyEvent.VK_B:
				prevButton.doClick();
				break;
			case KeyEvent.VK_P:
				playPauseButton.doClick();
				break;
			case KeyEvent.VK_R:
				repeatButton.doClick();
				break;
			case KeyEvent.VK_Q:
				dispose();
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void onParseComplete(final Subtitle subtitle, final Exception e) {
		if (e != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					String title = "Error";
					String message = "Cannot read subtitle file!";
					JOptionPane.showMessageDialog(ListeningTrainerDialog.this, message, title, JOptionPane.ERROR_MESSAGE);
					dispose();
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					onSubtitleReceived(subtitle);
				}
			});
		}
	}
	

	private void onSubtitleReceived(Subtitle subtitle) {
		listTimedText = ListeningTrainerTimedText.convert(subtitle, mediaPlayer.getLength() - ListeningTrainerTimedText.TIME_MARGIN);
		
		if (listTimedText != null && !listTimedText.isEmpty()) {
			long position = (long) (mediaPlayer.getLength() * mediaPlayer.getPosition());
			int currentIndex = 0;
			for (int i = 0, len = listTimedText.size(); i < len; i++) {
				if (listTimedText.get(i).getEndTime() >= position) {
					currentIndex = i;
					break;
				}
			}
			updateCurrentIndex(currentIndex);
		}
	}
}
